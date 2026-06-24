package org.task.pilot.domain.command;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartDeployment(@NotNull UUID promotionId) implements Command<Void> {
}
