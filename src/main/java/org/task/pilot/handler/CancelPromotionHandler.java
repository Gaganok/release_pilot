package org.task.pilot.handler;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.task.pilot.domain.command.CancelPromotion;
import org.task.pilot.domain.model.Promotion;
import org.task.pilot.persistance.ApplicationState;
import org.task.pilot.persistance.EventStore;
import org.task.pilot.port.NotificationPort;

import static org.task.pilot.persistance.EventType.CANCELLED;

@ApplicationScoped
public class CancelPromotionHandler implements CommandHandler<CancelPromotion, Void> {

  private final EventStore eventStore;
  private final NotificationPort notificationPort;

  public CancelPromotionHandler(EventStore eventStore, NotificationPort notificationPort) {
    this.eventStore = eventStore;
    this.notificationPort = notificationPort;
  }

  @WithTransaction
  public Uni<Void> handle(CancelPromotion command) {

    return eventStore.loadEvents(command.promotionId())
        .map(Promotion::reconstruct)
        .chain(promotion -> validateAndStore(promotion, command)
            .chain(_ -> notificationPort.notify(promotion.applicationId(), promotion.target(), promotion.applicationVersion(), promotion.status())))
        .replaceWithVoid();
  }

  @Override
  public Class<CancelPromotion> supports() {
    return CancelPromotion.class;
  }

  public Uni<Void> validateAndStore(Promotion promotion, CancelPromotion command) {
    var event = command.toEvent(promotion);
    return promotion.cancel(event)
        .chain(_ -> eventStore.append(event, CANCELLED))
        .flatMap(_ -> ApplicationState.apply(event.applicationId(), event));
  }

}
