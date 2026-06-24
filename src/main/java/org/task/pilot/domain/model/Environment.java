package org.task.pilot.domain.model;

import java.util.Optional;

import static java.util.Optional.empty;

public enum Environment {
  PRODUCTION,
  STAGING(PRODUCTION),
  DEV(STAGING),
  NONE(DEV);

  public final Optional<Environment> next;

  Environment() {
    this.next = empty();
  }

  Environment(Environment next) {
    this.next = Optional.of(next);
  }

  public Environment next() {
    return next.orElseThrow(() -> new IllegalStateException("No next environment for " + this));
  }

  public boolean isDirectSuccessor(Environment target) {
    return next.map(successor -> successor == target).orElse(false);
  }
}
