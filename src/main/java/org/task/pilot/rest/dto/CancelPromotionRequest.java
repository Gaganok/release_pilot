package org.task.pilot.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CancelPromotionRequest(
    @NotNull UUID cancelledBy,
    @NotBlank String reason
) {
}
