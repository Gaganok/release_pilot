package org.task.pilot.handlers;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import org.task.pilot.domain.command.Command;

import java.util.Map;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class CommandBus {

  private final Map<Class<? extends Command<?>>, CommandHandler<?, ?>> registry;

  public CommandBus(Instance<CommandHandler<?, ?>> handlers) {
    this.registry = handlers.stream()
        .collect(toMap(CommandHandler::supports, identity(), this::duplicateHandler));
  }

  private CommandHandler<?, ?> duplicateHandler(CommandHandler<?, ?> left, CommandHandler<?, ?> right) {
    throw new IllegalStateException("Duplicate handlers: %s and %s".formatted(
        left.getClass().getSimpleName(), right.getClass().getSimpleName()));
  }

  @SuppressWarnings("unchecked")
  public <R> Uni<R> dispatch(Command<R> command) {
    return Optional.ofNullable(registry.get(command.getClass()))
        .map(handler -> ((CommandHandler<Command<R>, R>) handler).handle(command))
        .orElseGet(() -> Uni.createFrom().failure(new IllegalStateException(
            "No handler found for %s".formatted(command.getClass().getSimpleName()))));
  }
}