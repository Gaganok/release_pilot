package org.task.pilot.domain.model;

public enum PromotionStatus {
  EMPTY,
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
