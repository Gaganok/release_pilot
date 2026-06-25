package org.task.pilot.persistance;

import java.util.UUID;

public interface ApproverRepository {
  boolean isApprover(UUID userId);
}
