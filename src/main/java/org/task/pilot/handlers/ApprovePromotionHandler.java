package org.task.pilot.handlers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.task.pilot.domain.command.ApprovePromotion;

@ApplicationScoped
public class ApprovePromotionHandler {

  @Transactional
  public void handle(ApprovePromotion command) {

  }
}
