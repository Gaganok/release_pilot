package org.task.pilot.handler;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.task.pilot.domain.command.CompletePromotion;
import org.task.pilot.domain.model.Promotion;
import org.task.pilot.persistance.ApplicationState;
import org.task.pilot.persistance.EventStore;
import org.task.pilot.port.NotificationPort;

import static org.task.pilot.persistance.EventType.STARTED;

@ApplicationScoped
public class CompletePromotionHandler implements CommandHandler<CompletePromotion, Void> {

  private final EventStore eventStore;
  private final NotificationPort notificationPort;

  public CompletePromotionHandler(EventStore eventStore, NotificationPort notificationPort) {
    this.eventStore = eventStore;
    this.notificationPort = notificationPort;
  }

  @WithTransaction
  public Uni<Void> handle(CompletePromotion command) {
    return eventStore.loadEvents(command.promotionId())
        .map(Promotion::reconstruct)
        .chain(promotion -> validateAndStore(promotion, command)
            .chain(_ -> notificationPort.notify(promotion.applicationId(), promotion.target(), promotion.applicationVersion(), promotion.status())))
        .replaceWithVoid();
  }

  @Override
  public Class<CompletePromotion> supports() {
    return CompletePromotion.class;
  }

  public Uni<Void> validateAndStore(Promotion promotion, CompletePromotion command) {
    var event = command.toEvent(promotion);
    return promotion.complete(event)
        .chain(_ -> eventStore.append(event, STARTED))
        .flatMap(_ -> ApplicationState.apply(event.applicationId(), event));
  }

}
