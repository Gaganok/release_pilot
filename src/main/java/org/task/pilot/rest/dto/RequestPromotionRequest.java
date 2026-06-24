package org.task.pilot.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.task.pilot.domain.model.Environment;

import java.util.UUID;

public record RequestPromotionRequest(
    @NotNull UUID applicationId,
    @NotBlank String applicationVersion,
    @NotNull Environment targetEnvironment,
    @NotNull UUID requestedBy
) {
}
