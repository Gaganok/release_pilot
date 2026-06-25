package org.task.pilot.persistance;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class MockApproverRepository implements ApproverRepository {

  private static final Set<UUID> APPROVERS = Set.of(
      UUID.fromString("00000000-0000-0000-0000-000000000001"),
      UUID.fromString("00000000-0000-0000-0000-000000000002")
  );

  @Override
  public boolean isApprover(UUID userId) {
    return APPROVERS.contains(userId);
  }
}
