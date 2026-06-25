package org.task.pilot.persistance;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "event_store",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_aggregate_version",
            columnNames = {"aggregate_id", "version"}
        )
    },
    indexes = {
        @Index(name = "idx_event_aggregate", columnList = "aggregate_id, version"),
        @Index(name = "idx_event_type", columnList = "eventType"),
        @Index(
            name = "idx_event_application_history",
            columnList = "application_id, timestamp DESC, version DESC"
        )
    })
public class EventEntity extends PanacheEntity {

  @Column(name = "aggregate_id", nullable = false, columnDefinition = "uuid")
  public UUID aggregateId;

  @Column(name = "application_id", nullable = false, columnDefinition = "uuid")
  public UUID applicationId;

  @Column(nullable = false)
  public long version;

  @Enumerated(STRING)
  public EventType eventType;

  @Column(nullable = false, columnDefinition = "jsonb")
  public String data;

  @Column(nullable = false)
  public Instant timestamp;

  public static EventEntity from(UUID aggregateId, UUID applicationId, EventType type, long version, String data) {
    var event = new EventEntity();
    event.aggregateId = aggregateId;
    event.applicationId = applicationId;
    event.version = version;
    event.eventType = type;
    event.data = data;
    event.timestamp = Instant.now();
    return event;
  }
}
