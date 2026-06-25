package org.task.pilot.handler;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.task.pilot.domain.command.RollbackPromotion;
import org.task.pilot.domain.model.Promotion;
import org.task.pilot.persistance.ApplicationState;
import org.task.pilot.persistance.EventStore;
import org.task.pilot.port.NotificationPort;

import static org.task.pilot.persistance.EventType.ROLLED_BACK;

@ApplicationScoped
public class RollbackPromotionHandler implements CommandHandler<RollbackPromotion, Void> {

  private final EventStore eventStore;
  private final NotificationPort notificationPort;

  public RollbackPromotionHandler(EventStore eventStore, NotificationPort notificationPort) {
    this.eventStore = eventStore;
    this.notificationPort = notificationPort;
  }

  @WithTransaction
  public Uni<Void> handle(RollbackPromotion command) {
    return eventStore.loadEvents(command.promotionId())
        .map(Promotion::reconstruct)
        .chain(promotion -> validateAndStore(promotion, command)
            .chain(_ -> notificationPort.notify(promotion.applicationId(), promotion.target(), promotion.applicationVersion(), promotion.status())))
        .replaceWithVoid();
  }

  @Override
  public Class<RollbackPromotion> supports() {
    return RollbackPromotion.class;
  }

  public Uni<Void> validateAndStore(Promotion promotion, RollbackPromotion command) {
    var event = command.toEvent(promotion);
    return promotion.rollback(event)
        .chain(_ -> eventStore.append(event, ROLLED_BACK))
        .flatMap(_ -> ApplicationState.apply(event.applicationId(), event));
  }

}
