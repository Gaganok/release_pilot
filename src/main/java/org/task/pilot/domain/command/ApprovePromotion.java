package org.task.pilot.domain.command;

import jakarta.validation.constraints.NotNull;
import org.task.pilot.domain.event.PromotionApproved;
import org.task.pilot.domain.model.Promotion;

import java.time.Instant;
import java.util.UUID;

public record ApprovePromotion(
    @NotNull UUID promotionId,
    @NotNull UUID approverId
) implements Command<Void> {
  public PromotionApproved toEvent(Promotion promotion) {
    return new PromotionApproved(
        promotionId,
        promotion.applicationId(),
        promotion.target(),
        approverId,
        Instant.now()
    );
  }
}
