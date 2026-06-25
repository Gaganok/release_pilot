package org.task.pilot.port;

import org.task.pilot.domain.model.Environment;

import java.util.UUID;

public interface DeploymentPort {
  void deploy(UUID applicationId, Environment environment, String applicationVersion);
}
