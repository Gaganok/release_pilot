package org.task.pilot.domain.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.task.pilot.domain.event.PromotionCancelled;
import org.task.pilot.domain.model.Promotion;

import java.time.Instant;
import java.util.UUID;

public record CancelPromotion(
    @NotNull UUID promotionId,
    @NotNull UUID cancelledBy,
    @NotBlank String reason
) implements Command<Void> {

  public PromotionCancelled toEvent(Promotion promotion) {
    return new PromotionCancelled(
        promotion.id(), promotion.applicationId(), promotion.target(), cancelledBy, reason, Instant.now());
  }

}
