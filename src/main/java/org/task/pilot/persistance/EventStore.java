package org.task.pilot.persistance;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.task.pilot.domain.event.PromotionEvent;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EventStore {

  private final ObjectMapper objectMapper;

  public EventStore(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Uni<Void> append(PromotionEvent event, EventType type) {
    return nextVersion(event.applicationId())
        .map(version -> EventEntity.from(event.promotionId(), type, version, serialise(event)))
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
