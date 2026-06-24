package org.task.pilot.domain.model;

public enum PromotionStatus {
  PENDING,
  APPROVED,
  DEPLOYING,
  COMPLETED,
  ROLLED_BACK,
  CANCELLED;

  public boolean isTerminal() {
    return this == COMPLETED || this == ROLLED_BACK || this == CANCELLED;
  }
}
