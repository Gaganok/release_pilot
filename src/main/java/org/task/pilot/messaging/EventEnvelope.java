package org.task.pilot.messaging;

import org.task.pilot.domain.model.Environment;
import org.task.pilot.persistance.EventType;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope(
    EventType type,
    UUID promotionId,
    UUID applicationId,
    String applicationVersion,
    Environment target,
    Instant occurredAt,
    Object payload) {
}
