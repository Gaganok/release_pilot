package org.task.pilot.domain.event;

import org.task.pilot.domain.command.RequestPromotion;
import org.task.pilot.domain.model.Environment;
import org.task.pilot.domain.model.Promotion;

import java.time.Instant;
import java.util.UUID;

public record PromotionRequested(
    UUID promotionId,
    UUID applicationId,
    String applicationVersion,
    Environment targetEnvironment,
    UUID requestedBy,
    Instant occurredAt
) implements PromotionEvent {

  public static PromotionRequested from(Promotion promotion, RequestPromotion command) {
    return new PromotionRequested(
        promotion.id(),
        command.applicationId(),
        command.applicationVersion(),
        promotion.target().next(),
        command.requestedBy(),
        Instant.now());
  }
}
