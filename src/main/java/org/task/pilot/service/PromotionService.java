package org.task.pilot.service;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.task.pilot.domain.event.PromotionEvent;
import org.task.pilot.domain.model.Promotion;
import org.task.pilot.domain.model.PromotionStatus;
import org.task.pilot.domain.model.ResultPage;
import org.task.pilot.persistance.EventStore;
import org.task.pilot.persistance.EventType;

import java.util.List;
import java.util.UUID;

import static org.task.pilot.exception.Thrower.webNotFound;

@ApplicationScoped
public class PromotionService {

  private final EventStore eventStore;

  public PromotionService(EventStore eventStore) {
    this.eventStore = eventStore;
  }

  @WithTransaction
  public Uni<ResultPage<PromotionEvent>> getApplicationPromotionPage(UUID applicationId, int pageIndex, int pageSize) {
    return eventStore.loadApplicationHistory(applicationId, pageIndex, pageSize);
  }

  @WithTransaction
  public Uni<PromotionHistory> getPromotionHistory(UUID promotionId) {
    return eventStore.loadEvents(promotionId)
        .map(events -> new PromotionHistory(
            Promotion.reconstruct(events), toEventRecords(events)))
        .flatMap(history -> {
          if (history.state().status() == PromotionStatus.EMPTY) {
            return Uni.createFrom().failure(webNotFound("Promotion not found"));
          }

          return Uni.createFrom().item(history);
        });
  }

  private List<EventRecord> toEventRecords(List<PromotionEvent> events) {
    return events.stream()
        .map(event -> new EventRecord(event, EventType.from(event.getClass())))
        .toList();
  }

  public record PromotionHistory(Promotion state, List<EventRecord> history) {
  }

  public record EventRecord(@JsonUnwrapped PromotionEvent event, EventType type) {

  }
}
