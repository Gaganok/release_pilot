package org.task.pilot.handlers;

import io.smallrye.mutiny.Uni;
import org.task.pilot.domain.command.Command;

public interface CommandHandler<C extends Command<R>, R> {
  Uni<R> handle(C command);
}