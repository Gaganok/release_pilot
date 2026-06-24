package org.task.pilot.domain.event;

import org.task.pilot.domain.model.Environment;

import java.time.Instant;
import java.util.UUID;

public record PromotionRolledBack(
    UUID promotionId,
    UUID applicationId,
    Environment targetEnvironment,
    String reason,
    Instant occurredAt
) implements PromotionEvent {
}
