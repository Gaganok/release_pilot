package org.task.pilot.domain.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.task.pilot.domain.event.PromotionRolledBack;
import org.task.pilot.domain.model.Promotion;

import java.time.Instant;
import java.util.UUID;

public record RollbackPromotion(
    @NotNull UUID promotionId,
    @NotBlank String reason
) implements Command<Void> {

  public PromotionRolledBack toEvent(Promotion promotion) {
    return new PromotionRolledBack(
        promotion.id(), promotion.applicationId(), promotion.target(), reason, Instant.now());
  }
}
