package org.task.pilot.domain.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CancelPromotion(
    @NotNull UUID promotionId,
    @NotNull UUID cancelledBy,
    @NotBlank String reason
) implements Command<Void> {
}
