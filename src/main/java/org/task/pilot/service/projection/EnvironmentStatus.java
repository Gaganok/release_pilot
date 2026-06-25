package org.task.pilot.service.projection;

import org.task.pilot.domain.model.Environment;
import org.task.pilot.persistance.SlotStatus;

import java.util.UUID;

public record EnvironmentStatus(
    Environment environment,
    SlotStatus slotStatus,
    UUID activePromotionId,
    String completedVersion
) {
  public static EnvironmentStatus empty(Environment environment) {
    return new EnvironmentStatus(environment, SlotStatus.EMPTY, null, null);
  }
}