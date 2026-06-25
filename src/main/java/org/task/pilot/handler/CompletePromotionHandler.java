package org.task.pilot.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.task.pilot.domain.command.CompletePromotion;

@ApplicationScoped
public class CompletePromotionHandler {

  @Transactional
  public void handle(CompletePromotion command) {

  }

}
