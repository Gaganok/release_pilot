package org.task.pilot.port;

import io.smallrye.mutiny.Uni;
import org.task.pilot.domain.model.Environment;
import org.task.pilot.domain.model.PromotionStatus;

import java.util.UUID;

public interface NotificationPort {
  Uni<Void> notify(UUID applicationId, Environment environment, String applicationVersion, PromotionStatus status);
}
