package org.task.pilot.domain.command;

import jakarta.validation.constraints.NotNull;
import org.task.pilot.domain.event.DeploymentStarted;
import org.task.pilot.domain.model.Promotion;

import java.time.Instant;
import java.util.UUID;

public record StartDeployment(@NotNull UUID promotionId) implements Command<Void> {

  public DeploymentStarted toEvent(Promotion promotion) {
    return new DeploymentStarted(promotion.id(), promotion.applicationId(), promotion.target(), Instant.now());
  }
}
