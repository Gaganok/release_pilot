package org.task.pilot.domain.event;

import org.task.pilot.domain.model.Environment;

import java.time.Instant;
import java.util.UUID;

public record PromotionApproved(
    UUID promotionId,
    UUID applicationId,
    Environment targetEnvironment,
    UUID approvedBy,
    Instant occurredAt
) implements PromotionEvent {
}
