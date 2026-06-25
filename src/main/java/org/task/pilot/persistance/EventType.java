package org.task.pilot.persistance;

import org.task.pilot.domain.event.DeploymentStarted;
import org.task.pilot.domain.event.PromotionApproved;
import org.task.pilot.domain.event.PromotionCancelled;
import org.task.pilot.domain.event.PromotionCompleted;
import org.task.pilot.domain.event.PromotionEvent;
import org.task.pilot.domain.event.PromotionRequested;
import org.task.pilot.domain.event.PromotionRolledBack;

public enum EventType {
  APPROVED(PromotionApproved.class),
  CANCELLED(PromotionCancelled.class),
  COMPLETED(PromotionCompleted.class),
  REQUESTED(PromotionRequested.class),
  ROLLED_BACK(PromotionRolledBack.class),
  STARTED(DeploymentStarted.class);

  public final Class<? extends PromotionEvent> clazz;

  EventType(Class<? extends PromotionEvent> clazz) {
    this.clazz = clazz;
  }
}
