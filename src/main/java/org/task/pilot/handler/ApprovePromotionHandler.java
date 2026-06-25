package org.task.pilot.handler;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.task.pilot.domain.command.ApprovePromotion;
import org.task.pilot.domain.model.Promotion;
import org.task.pilot.persistance.ApproverRepository;
import org.task.pilot.persistance.EventStore;

import static org.task.pilot.persistance.EventType.APPROVED;

@ApplicationScoped
public class ApprovePromotionHandler implements CommandHandler<ApprovePromotion, Void> {

  private final ApproverRepository approverRepository;
  private final EventStore eventStore;

  public ApprovePromotionHandler(ApproverRepository approverRepository, EventStore eventStore) {
    this.approverRepository = approverRepository;
    this.eventStore = eventStore;
  }

  @WithTransaction
  public Uni<Void> handle(ApprovePromotion command) {
    if (!approverRepository.isApprover(command.approverId())) {
      return Uni.createFrom().failure(new IllegalStateException("No approver rights"));
    }

    return eventStore.loadEvents(command.promotionId())
        .map(Promotion::reconstruct)
        .chain(promotion -> validateAndStore(promotion, command))
        .replaceWithVoid();
  }

  @Override
  public Class<ApprovePromotion> supports() {
    return ApprovePromotion.class;
  }

  public Uni<Void> validateAndStore(Promotion promotion, ApprovePromotion command) {
    var event = command.toEvent(promotion);
    return promotion.approve(event)
        .chain(_ -> eventStore.append(event, APPROVED));
  }
}
