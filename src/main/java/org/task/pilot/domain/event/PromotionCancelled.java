package org.task.pilot.domain.event;


import org.task.pilot.domain.model.Environment;

import java.time.Instant;
import java.util.UUID;

public record PromotionCancelled(
    UUID promotionId,
    UUID applicationId,
    Environment targetEnvironment,
    UUID cancelledBy,
    String reason,
    Instant occurredAt
) implements PromotionEvent {
}
