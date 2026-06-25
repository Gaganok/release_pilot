package org.task.pilot.domain.model;

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

public record Promotion(UUID id,
                        UUID applicationId,
                        String applicationVersion,
                        Environment source,
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

  public Promotion request(PromotionRequested event) {
    enforceEnvironmentOrder(event.targetEnvironment());
    return onRequest(event);
  }

  public Promotion approve(PromotionApproved event) {
    if (this.status != PENDING) {
      throw new IllegalArgumentException("Promotion approved is not pending");
    }

    return onApprove(event);
  }

  public Promotion started(DeploymentStarted event) {
    if (this.status != APPROVED) {
      throw new IllegalArgumentException("Promotion should be in approved status to start deployment");
    }

    return onStarted(event);
  }

  public Promotion complete(PromotionCompleted event) {
    if (this.status != DEPLOYING) {
      throw new IllegalArgumentException("Promotion should deploying to complete");
    }
    return onComplete(event);
  }

  public Promotion rollback(PromotionRolledBack event) {
    if (this.status != COMPLETED) {
      throw new IllegalArgumentException("Promotion should be completed status to rollback");
    }

    return onRollback(event);
  }

  public Promotion cancel(PromotionCancelled event) {
    if (this.status.isTerminal()) {
      throw new IllegalArgumentException("Promotion is already in terminal state: " + this.status);
    }

    return onCancel(event);
  }

  private void enforceEnvironmentOrder(Environment target) {
    if (this.status.isTerminal()) {
      throw new IllegalStateException("Cannot request promotion from terminal state: " + this.status);
    }

    if (this.status == DEPLOYING) {
      throw new IllegalStateException("Cannot request a new promotion while deploying: " + this.status);
    }

    if (target.isDirectSuccessor(this.source)) {
      throw new IllegalArgumentException("Invalid environment transition: " + this.source + " -> " + target);
    }
  }

  private Promotion withTarget(Environment target) {
    return new Promotion(this.id, this.applicationId, this.applicationVersion, this.source,
        target, this.requestedBy, this.requestedAt, this.status);
  }

  private Promotion withStatus(PromotionStatus status) {
    return new Promotion(this.id, this.applicationId, this.applicationVersion, this.source,
        this.target, this.requestedBy, this.requestedAt, status);
  }

  private Promotion withRequestedBy(UUID requestedBy) {
    return new Promotion(this.id, this.applicationId, this.applicationVersion, this.source,
        this.target, requestedBy, this.requestedAt, this.status);
  }

  private Promotion onRequest(PromotionRequested event) {
    return new Promotion(
        event.promotionId(),
        event.applicationId(),
        event.applicationVersion(),
        this.source,
        event.targetEnvironment(),
        event.requestedBy(),
        event.occurredAt(),
        PENDING
    );
  }

  private Promotion onApprove(PromotionApproved event) {
    return this.withStatus(APPROVED);
  }

  private Promotion onStarted(DeploymentStarted event) {
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
      case DeploymentStarted e -> onStarted(e);
      case PromotionCompleted e -> onComplete(e);
      case PromotionRolledBack e -> onRollback(e);
      case PromotionCancelled e -> onCancel(e);
    };
  }

  public static Promotion empty() {
    return new Promotion(randomUUID(), null, null,
        NONE, NONE, null, now(), PENDING);
  }

}
