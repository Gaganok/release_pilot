package org.task.pilot.domain.event;

import org.task.pilot.domain.model.Environment;

import java.time.Instant;
import java.util.UUID;

public sealed interface PromotionEvent permits PromotionRequested, PromotionApproved,
    DeploymentStarted, PromotionCompleted, PromotionRolledBack, PromotionCancelled {

  UUID promotionId();

  UUID applicationId();

  Environment targetEnvironment();

  Instant occurredAt();
}
