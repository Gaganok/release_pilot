package org.task.pilot.rest.resource;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.task.pilot.service.PromotionService;

import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("/promotions")
public class PromotionResource {

  private final PromotionService promotionService;

  public PromotionResource(PromotionService promotionService) {
    this.promotionService = promotionService;
  }

  @GET
  @Path("/{id}")
  public Uni<Response> promotionHistory(@PathParam("id") UUID promotionId) {
    return promotionService.getPromotionHistory(promotionId)
        .map(body -> Response.status(CREATED).entity(body).build());
  }
}
