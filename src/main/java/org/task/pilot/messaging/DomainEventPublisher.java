package org.task.pilot.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.task.pilot.domain.event.PromotionEvent;
import org.task.pilot.domain.model.Promotion;
import org.task.pilot.persistance.EventType;

@ApplicationScoped
public class DomainEventPublisher {

  private final MutinyEmitter<String> emitter;
  private final ObjectMapper objectMapper;

  public DomainEventPublisher(@Channel("promotion-events") MutinyEmitter<String> emitter,
                              ObjectMapper objectMapper) {
    this.emitter = emitter;
    this.objectMapper = objectMapper;
  }

  public Uni<Void> publish(Promotion promotion, PromotionEvent event, EventType type) {
    return Uni.createFrom().item(toEventEnvelopeJson(promotion, event, type))
        .flatMap(emitter::send);
  }

  private String toEventEnvelopeJson(Promotion promotion, PromotionEvent event, EventType type) {
    try {
      return objectMapper.writeValueAsString(
          new EventEnvelope(
              type,
              promotion.id(),
              promotion.applicationId(),
              promotion.applicationVersion(),
              promotion.target(),
              event.occurredAt(),
              event
          ));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize event", e);
    }
  }
}
