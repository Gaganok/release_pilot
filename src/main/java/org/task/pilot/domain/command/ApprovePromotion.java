package org.task.pilot.domain.command;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ApprovePromotion(
    @NotNull UUID promotionId,
    @NotNull UUID approverId
) implements Command<Void> {
}
