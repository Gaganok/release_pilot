package org.task.pilot.domain.model;

import java.util.Optional;

import static java.util.Optional.empty;

public enum Environment {
  PRODUCTION(3),
  STAGING(PRODUCTION, 2),
  DEV(STAGING, 1),
  NONE(DEV, 0);

  public final Optional<Environment> next;
  public final int order;

  Environment(int order) {
    this.order = order;
    this.next = empty();
  }

  Environment(Environment next, int order) {
    this.next = Optional.of(next);
    this.order = order;
  }

  public Environment next() {
    return next.orElseThrow(() -> new IllegalStateException("No next environment for " + this));
  }

  public int order() {
    return order;
  }

  public boolean isDirectSuccessor(Environment target) {
    return target.next.map(successor -> successor == this).orElse(false);
  }
}
