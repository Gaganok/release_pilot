package org.task.pilot.port;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.task.pilot.domain.model.Environment;
import org.task.pilot.domain.model.PromotionStatus;

import java.util.UUID;

public class MockNotificationPort implements NotificationPort {

  private static final Logger log = LoggerFactory.getLogger(MockNotificationPort.class);

  public void notify(UUID applicationId, Environment environment, String applicationVersion, PromotionStatus status) {
    var info = "Application: %s Environment: %s Version: %s Status: %s".formatted(applicationId, environment, applicationVersion, status);
    log.info(info);
  }
}
