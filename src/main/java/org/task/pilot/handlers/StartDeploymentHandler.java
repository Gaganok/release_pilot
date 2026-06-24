package org.task.pilot.handlers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.task.pilot.domain.command.StartDeployment;

@ApplicationScoped
public class StartDeploymentHandler {

  @Transactional
  public void handle(StartDeployment command) {

  }

}
