package org.task.pilot.handler;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.task.pilot.domain.command.StartDeployment;
import org.task.pilot.domain.model.Promotion;
import org.task.pilot.persistance.ApplicationState;
import org.task.pilot.persistance.EventStore;
import org.task.pilot.port.DeploymentPort;

import static org.task.pilot.persistance.EventType.STARTED;

@ApplicationScoped
public class StartDeploymentHandler implements CommandHandler<StartDeployment, Void> {

  private final EventStore eventStore;
  private final DeploymentPort deploymentPort;

  public StartDeploymentHandler(EventStore eventStore, DeploymentPort deploymentPort) {
    this.eventStore = eventStore;
    this.deploymentPort = deploymentPort;
  }

  @WithTransaction
  public Uni<Void> handle(StartDeployment command) {

    return eventStore.loadEvents(command.promotionId())
        .map(Promotion::reconstruct)
        .chain(promotion -> validateAndStore(promotion, command)
            .chain(_ -> deploymentPort.deploy(promotion.applicationId(), promotion.target(), promotion.applicationVersion())))
        .replaceWithVoid();
  }

  @Override
  public Class<StartDeployment> supports() {
    return StartDeployment.class;
  }

  public Uni<Void> validateAndStore(Promotion promotion, StartDeployment command) {
    var event = command.toEvent(promotion);
    return promotion.deploy(event)
        .chain(_ -> eventStore.append(event, STARTED))
        .flatMap(_ -> ApplicationState.apply(event.applicationId(), event));
  }

}
