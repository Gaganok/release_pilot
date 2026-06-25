package org.task.pilot.domain.model;

import io.smallrye.mutiny.Uni;
import org.task.pilot.domain.event.DeploymentStarted;
import org.task.pilot.domain.event.PromotionApproved;
import org.task.pilot.domain.event.PromotionCancelled;
import org.task.pilot.domain.event.PromotionCompleted;
import org.task.pilot.domain.event.PromotionEvent;
import org.task.pilot.domain.event.PromotionRequested;
import org.task.pilot.domain.event.PromotionRolledBack;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static org.task.pilot.domain.model.Environment.NONE;
import static org.task.pilot.domain.model.PromotionStatus.*;
import static org.task.pilot.exception.Thrower.webThrow;

public record Promotion(UUID id,
                        UUID applicationId,
                        String applicationVersion,
                        Environment target,
                        UUID requestedBy,
                        Instant requestedAt,
                        PromotionStatus status) {

  public static Promotion reconstruct(List<PromotionEvent> events) {
    var promotion = empty();

    for (var event : events) {
      promotion = promotion.apply(event);
    }

    return promotion;
  }

  public Uni<Promotion> request(PromotionRequested event) {
    return enforceEnvironmentOrder().replaceWith(onRequest(event));
  }

  public Uni<Promotion> approve(PromotionApproved event) {
    if (this.status != PENDING) {
      return Uni.createFrom().failure(webThrow("Promotion approved is not pending"));
    }

    return Uni.createFrom().item(onApprove(event));
  }

  public Uni<Promotion> deploy(DeploymentStarted event) {
    if (this.status != APPROVED) {
      return Uni.createFrom().failure(webThrow("Promotion should be in approved status to start deployment"));
    }

    return Uni.createFrom().item(onDeploy(event));
  }

  public Uni<Promotion> complete(PromotionCompleted event) {
    if (this.status != DEPLOYING) {
      return Uni.createFrom().failure(webThrow("Promotion should deploying to complete"));
    }
    return Uni.createFrom().item(onComplete(event));
  }

  public Uni<Promotion> rollback(PromotionRolledBack event) {
    if (this.status != DEPLOYING) {
      return Uni.createFrom().failure(webThrow("Promotion deployment should start to rollback"));
    }

    return Uni.createFrom().item(onRollback(event));
  }

  public Uni<Promotion> cancel(PromotionCancelled event) {
    if (this.status.isTerminal()) {
      return Uni.createFrom()
          .failure(webThrow("Promotion is in terminal state: %s".formatted(this.status)));
    }

    return Uni.createFrom().item(onCancel(event));
  }

  private Uni<Void> enforceEnvironmentOrder() {
    if (this.status.isTerminal()) {
      return Uni.createFrom()
          .failure(webThrow("Cannot request promotion from terminal state: %s".formatted(this.status)));
    }

    if (this.status == DEPLOYING) {
      return Uni.createFrom().failure(
          webThrow("Cannot request a new promotion while deploying: %s".formatted(this.status)));
    }

    return Uni.createFrom().voidItem();
  }

  private Promotion withStatus(PromotionStatus status) {
    return new Promotion(this.id, this.applicationId, this.applicationVersion,
        this.target, this.requestedBy, this.requestedAt, status);
  }

  private Promotion onRequest(PromotionRequested event) {
    return new Promotion(
        event.promotionId(),
        event.applicationId(),
        event.applicationVersion(),
        event.targetEnvironment(),
        event.requestedBy(),
        event.occurredAt(),
        PENDING
    );
  }

  private Promotion onApprove(PromotionApproved event) {
    return this.withStatus(APPROVED);
  }

  private Promotion onDeploy(DeploymentStarted event) {
    return this.withStatus(DEPLOYING);
  }

  private Promotion onComplete(PromotionCompleted event) {
    return this.withStatus(COMPLETED);
  }

  private Promotion onRollback(PromotionRolledBack event) {
    return this.withStatus(ROLLED_BACK);
  }

  private Promotion onCancel(PromotionCancelled event) {
    return this.withStatus(CANCELLED);
  }

  private Promotion apply(PromotionEvent event) {
    return switch (event) {
      case PromotionRequested e -> onRequest(e);
      case PromotionApproved e -> onApprove(e);
      case DeploymentStarted e -> onDeploy(e);
      case PromotionCompleted e -> onComplete(e);
      case PromotionRolledBack e -> onRollback(e);
      case PromotionCancelled e -> onCancel(e);
    };
  }

  public static Promotion empty() {
    return new Promotion(randomUUID(), null, null,
        NONE, null, now(), EMPTY);
  }

}
