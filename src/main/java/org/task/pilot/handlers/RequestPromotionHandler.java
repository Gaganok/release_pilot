package org.task.pilot.handlers;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.task.pilot.domain.command.RequestPromotion;

@ApplicationScoped
@Handles(RequestPromotion.class)
public class RequestPromotionHandler implements CommandHandler<RequestPromotion, Long> {

  @Transactional
  public Uni<Long> handle(RequestPromotion command) {

    return null;
  }

}
