package org.task.pilot.domain.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.task.pilot.domain.event.PromotionRequested;
import org.task.pilot.domain.model.Environment;

import java.time.Instant;
import java.util.UUID;

public record RequestPromotion(
    @NotNull UUID applicationId,
    @NotBlank String applicationVersion,
    @NotNull Environment targetEnvironment,
    @NotNull UUID requestedBy
) implements Command<UUID> {

  public PromotionRequested toEvent() {
    return new PromotionRequested(
        UUID.randomUUID(),
        applicationId,
        applicationVersion,
        targetEnvironment,
        requestedBy,
        Instant.now()
    );
  }
}
