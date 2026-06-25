package org.task.pilot.domain.event;

import org.task.pilot.domain.model.Environment;

import java.time.Instant;
import java.util.UUID;

public record DeploymentStarted(
    UUID promotionId,
    UUID applicationId,
    Environment targetEnvironment,
    String deploymentReference,
    Instant occurredAt
) implements PromotionEvent {
}
