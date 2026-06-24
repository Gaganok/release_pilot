package org.task.pilot.handlers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.task.pilot.domain.command.RollbackPromotion;

@ApplicationScoped
public class RollbackPromotionHandler {

  @Transactional
  public void handle(RollbackPromotion command) {

  }

}
