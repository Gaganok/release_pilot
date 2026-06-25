package org.task.pilot.port;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.task.pilot.domain.model.Environment;

import java.util.UUID;

@ApplicationScoped
public class MockDeploymentPort implements DeploymentPort {
  private static final Logger log = LoggerFactory.getLogger(MockDeploymentPort.class);

  @Override
  public void deploy(UUID applicationId, Environment environment, String applicationVersion) {
    var info = "Deploying %s to %s".formatted(applicationId, environment);
    log.info(info);
  }
}
