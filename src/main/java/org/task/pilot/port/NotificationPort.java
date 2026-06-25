package org.task.pilot.port;

import org.task.pilot.domain.model.Environment;
import org.task.pilot.domain.model.PromotionStatus;

import java.util.UUID;

public interface NotificationPort {
  void notify(UUID applicationId, Environment environment, String applicationVersion, PromotionStatus status);
}
