package org.task.pilot.port;

import io.smallrye.mutiny.Uni;
import org.task.pilot.domain.model.Environment;

import java.util.UUID;

public interface DeploymentPort {
  Uni<Void> deploy(UUID applicationId, Environment environment, String applicationVersion);
}
