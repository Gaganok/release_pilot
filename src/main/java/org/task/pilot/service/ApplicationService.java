package org.task.pilot.service;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.task.pilot.domain.model.Environment;
import org.task.pilot.persistance.ApplicationState;
import org.task.pilot.service.projection.EnvironmentStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class ApplicationService {

  @WithTransaction
  public Uni<Map<Environment, EnvironmentStatus>> getApplicationEnvironmentStatus(UUID applicationId) {
    return ApplicationState.findLatestStatusPerEnv(applicationId)
        .map(this::generateEnvironemntMap);
  }

  private Map<Environment, EnvironmentStatus> generateEnvironemntMap(List<EnvironmentStatus> statusList) {
    Map<Environment, EnvironmentStatus> map = Arrays.stream(Environment.values())
        .collect(toMap(Function.identity(), EnvironmentStatus::empty));

    statusList.forEach(status -> map.put(status.environment(), status));
    map.remove(Environment.NONE);

    return map;
  }
}
