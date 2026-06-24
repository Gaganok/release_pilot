package org.task.pilot.handlers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.task.pilot.domain.command.CancelPromotion;

@ApplicationScoped
public class CancelPromotionHandler {

  @Transactional
  public void handle(CancelPromotion command) {

  }

}
