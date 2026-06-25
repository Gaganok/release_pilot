package org.task.pilot.messaging.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AuditLogConsumer {
  
  private static final Logger log = LoggerFactory.getLogger(AuditLogConsumer.class);

  @Incoming("promotion-events")
  public void consume(String json) {
    log.info("Received and logged: " + json);
  }
}
