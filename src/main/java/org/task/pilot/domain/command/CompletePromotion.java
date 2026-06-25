package org.task.pilot.domain.command;

import jakarta.validation.constraints.NotNull;
import org.task.pilot.domain.event.PromotionCompleted;
import org.task.pilot.domain.model.Promotion;

import java.time.Instant;
import java.util.UUID;

public record CompletePromotion(@NotNull UUID promotionId) implements Command<Void> {
  public PromotionCompleted toEvent(Promotion promotion) {
    return new PromotionCompleted(promotionId, promotion.applicationId(), promotion.target(), promotion.applicationVersion(), Instant.now());
  }
}
