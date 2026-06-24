package org.task.pilot.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ApprovePromotionRequest(@NotNull UUID approverId) {
}
