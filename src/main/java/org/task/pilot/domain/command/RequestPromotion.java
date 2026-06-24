package org.task.pilot.domain.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.task.pilot.domain.model.Environment;

import java.util.UUID;

public record RequestPromotion(
    @NotNull UUID applicationId,
    @NotBlank String applicationVersion,
    @NotNull Environment targetEnvironment,
    @NotNull UUID requestedBy
) implements Command<Long> {
}
