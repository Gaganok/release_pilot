package org.task.pilot.persistance;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.task.pilot.domain.event.DeploymentStarted;
import org.task.pilot.domain.event.PromotionApproved;
import org.task.pilot.domain.event.PromotionCancelled;
import org.task.pilot.domain.event.PromotionCompleted;
import org.task.pilot.domain.event.PromotionEvent;
import org.task.pilot.domain.event.PromotionRequested;
import org.task.pilot.domain.event.PromotionRolledBack;
import org.task.pilot.domain.model.Environment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static jakarta.persistence.EnumType.STRING;
import static org.task.pilot.persistance.SlotStatus.COMPLETED;
import static org.task.pilot.persistance.SlotStatus.IN_PROGRESS;

@Entity
@Table(
    name = "application_state",
    indexes = {
//        @Index(
//            name = "uk_application_env_active",
//            columnList = "application_id, environment",
//            unique = true,
//            options = "where slot <> 'COMPLETED'"
//        )
    }
)
public class ApplicationState extends PanacheEntity {

  @Column(name = "application_id", nullable = false, columnDefinition = "uuid")
  public UUID applicationId;

  @Enumerated(STRING)
  @Column(nullable = false)
  public Environment environment;

  @Enumerated(STRING)
  @Column(name = "slot", nullable = false)
  public SlotStatus slotStatus;

  @Column(name = "active_promotion_id", columnDefinition = "uuid")
  public UUID activePromotionId;

  @Column(name = "completed_version")
  public String completedVersion;

  public static Uni<Optional<ApplicationState>> findOrEmpty(UUID applicationId, Environment environment) {
    return find("applicationId = ?1 AND environment = ?2", applicationId, environment)
        .firstResult()
        .map(slot -> Optional.ofNullable((ApplicationState) slot));
  }

  public static Uni<List<ApplicationState>> findCompletedFor(UUID applicationId, String version) {
    return find(
        "applicationId = ?1 AND completedVersion = ?2 AND slotStatus = ?3", applicationId, version, COMPLETED)
        .list();
  }

  public static Uni<Void> apply(UUID applicationId, PromotionEvent event) {
    return switch (event) {
      case PromotionRequested e -> createSlot(applicationId, e);
      case PromotionCancelled e -> deleteSlot(applicationId, e.targetEnvironment());
      case PromotionRolledBack e -> deleteSlot(applicationId, e.targetEnvironment());
      case PromotionApproved _, DeploymentStarted _ -> Uni.createFrom().voidItem();
      case PromotionCompleted e -> updateState(applicationId, e.targetEnvironment(),
          slot -> {
            slot.slotStatus = COMPLETED;
            slot.completedVersion = e.applicationVersion();
          });
    };
  }

  private static Uni<Void> createSlot(UUID applicationId, PromotionRequested e) {
    var slot = new ApplicationState();
    slot.applicationId = applicationId;
    slot.environment = e.targetEnvironment();
    slot.slotStatus = IN_PROGRESS;
    slot.activePromotionId = e.promotionId();
    return slot.<ApplicationState>persist().replaceWithVoid();
  }

  private static Uni<Void> updateState(UUID appId, Environment env, Consumer<ApplicationState> mutator) {
    return findOrEmpty(appId, env)
        .map(maybeSlot -> maybeSlot.orElseThrow(() ->
            new IllegalStateException("No slot found for " + appId + " -> " + env)))
        .invoke(mutator)
        .flatMap(slot -> slot.persist())
        .replaceWithVoid();
  }

  private static Uni<Void> deleteSlot(UUID applicationId, Environment environment) {
    return delete("applicationId = ?1 AND environment = ?2", applicationId, environment)
        .replaceWithVoid();
  }
}