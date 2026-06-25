package org.task.pilot.persistance;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.task.pilot.domain.event.PromotionEvent;
import org.task.pilot.domain.model.ResultPage;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EventStore {

  private final ObjectMapper objectMapper;

  public EventStore(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @WithTransaction
  public Uni<Void> append(PromotionEvent event, EventType type) {
    return nextVersion(event.promotionId())
        .map(version -> EventEntity.from(event.promotionId(), event.applicationId(), type, version, serialise(event)))
        .flatMap(entity -> entity.persist())
        .replaceWithVoid()
        .onFailure(this::isUniqueConstraintViolation)
        .recoverWithUni(cve -> Uni.createFrom()
            .failure(new IllegalStateException("Duplicate version detected for aggregate " + event.promotionId(), cve)));
  }

  public Uni<List<PromotionEvent>> loadEvents(UUID aggregateId) {
    Uni<List<EventEntity>> events = EventEntity
        .find("aggregateId = ?1 ORDER BY version ASC", aggregateId)
        .list();

    return events.map(this::toPromotionEvents);
  }

  @WithTransaction
  public Uni<ResultPage<PromotionEvent>> loadApplicationHistory(UUID applicationId, int pageIndex, int pageSize) {
    var query = EventEntity.<EventEntity>find("applicationId = ?1 ORDER BY timestamp DESC, version DESC", applicationId)
        .page(Page.of(pageIndex, pageSize));

    return Uni.combine().all().unis(query.list(), query.count()).asTuple()
        .map(tuple -> {
          List<EventEntity> entities = tuple.getItem1();
          long totalCount = tuple.getItem2();

          List<PromotionEvent> domainEvents = entities.stream()
              .map(this::deserialise)
              .toList();

          int totalPages = (int) Math.ceil((double) totalCount / pageSize);

          return new ResultPage<>(pageIndex, pageSize, totalCount, totalPages, domainEvents);
        });
  }

  private Uni<Long> nextVersion(UUID aggregateId) {
    return EventEntity.find(
            "SELECT COALESCE(MAX(version), 0) FROM EventEntity WHERE aggregateId = ?1", aggregateId)
        .project(Long.class)
        .singleResult()
        .map(version -> ++version);
  }

  private List<PromotionEvent> toPromotionEvents(List<EventEntity> events) {
    return events.stream().map(this::deserialise).toList();
  }

  private String serialise(PromotionEvent event) {
    try {
      return objectMapper.writeValueAsString(event);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to serialize event", e);
    }
  }

  private PromotionEvent deserialise(EventEntity entity) {
    try {
      return objectMapper.readValue(entity.data, entity.eventType.clazz);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to deserialize event", e);
    }
  }

  private boolean isUniqueConstraintViolation(Throwable t) {
    if (t instanceof ConstraintViolationException || (
        t instanceof PersistenceException pe && pe.getCause() instanceof ConstraintViolationException
    )) {
      return true;
    }

    return t.getMessage() != null && t.getMessage().contains("23505");
  }
}
