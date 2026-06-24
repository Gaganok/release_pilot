package org.task.pilot.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RollbackPromotionRequest(@NotBlank String reason) {
}
