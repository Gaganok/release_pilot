package org.task.pilot.handler;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.task.pilot.domain.command.RequestPromotion;
import org.task.pilot.domain.event.PromotionRequested;
import org.task.pilot.domain.model.Environment;
import org.task.pilot.persistance.ApplicationState;
import org.task.pilot.persistance.EventStore;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.task.pilot.persistance.EventType.REQUESTED;
import static org.task.pilot.persistance.SlotStatus.EMPTY;

@ApplicationScoped
public class RequestPromotionHandler implements CommandHandler<RequestPromotion, UUID> {

  private final EventStore eventStore;

  public RequestPromotionHandler(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  @WithTransaction
  public Uni<UUID> handle(RequestPromotion command) {
    return validateNoActivePromotion(command)
        .flatMap(_ -> deriveSourceAndValidateOrder(command))
        .flatMap(_ -> storeEvent(command))
        .map(PromotionRequested::promotionId);
  }

  @Override
  public Class<RequestPromotion> supports() {
    return RequestPromotion.class;
  }

  private Uni<Void> validateNoActivePromotion(RequestPromotion command) {
    return ApplicationState.findOrEmpty(command.applicationId(), command.targetEnvironment())
        .flatMap(this::checkNoActivePromotion);
  }

  private Uni<Void> checkNoActivePromotion(Optional<ApplicationState> slotOpt) {
    if (slotOpt.isEmpty()) {
      return Uni.createFrom().voidItem();
    }

    var slot = slotOpt.get();
    if (slot.slotStatus != EMPTY) {
      return Uni.createFrom().failure(new IllegalStateException(
          "Another promotion (%s) is already in progress for this environment".formatted(slot.activePromotionId)));
    }

    return Uni.createFrom().voidItem();
  }

  private Uni<Environment> deriveSourceAndValidateOrder(RequestPromotion command) {
    return ApplicationState.findCompletedFor(command.applicationId(), command.applicationVersion())
        .map(this::findHighestCompletedEnvironment)
        .flatMap(source -> validateDirectSuccessor(source, command.targetEnvironment()));
  }

  private Environment findHighestCompletedEnvironment(List<ApplicationState> completedSlots) {
    return completedSlots.stream()
        .map(slot -> slot.environment)
        .max(Comparator.comparingInt(Environment::order))
        .orElse(Environment.NONE);
  }

  private Uni<Environment> validateDirectSuccessor(Environment source, Environment target) {
    if (!target.isDirectSuccessor(source)) {
      return Uni.createFrom().failure(new IllegalArgumentException(
          "Invalid environment transition: %s → %s".formatted(source, target)));
    }
    return Uni.createFrom().item(source);
  }

  private Uni<PromotionRequested> storeEvent(RequestPromotion command) {
    var event = command.toEvent();
    return eventStore.append(event, REQUESTED)
        .flatMap(_ -> ApplicationState.apply(event.applicationId(), event))
        .replaceWith(event);
  }
}
