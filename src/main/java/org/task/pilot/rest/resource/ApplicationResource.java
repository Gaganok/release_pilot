package org.task.pilot.rest.resource;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.task.pilot.rest.dto.PageRequest;
import org.task.pilot.service.ApplicationService;
import org.task.pilot.service.PromotionService;

import java.util.UUID;

@Path("/applications")
public class ApplicationResource {

  private final ApplicationService applicationService;
  private final PromotionService promotionService;

  public ApplicationResource(ApplicationService applicationService, PromotionService promotionService) {
    this.applicationService = applicationService;
    this.promotionService = promotionService;
  }

  @GET
  @Path("/{id}/status")
  public Uni<Response> applicationStatus(@PathParam("id") UUID applicationId) {
    return applicationService.getApplicationEnvironmentStatus(applicationId)
        .map(body -> Response.ok(body).build());
  }

  @POST
  @Path("/{id}/promotions")
  public Uni<Response> applicationPromotions(@PathParam("id") UUID applicationId,
                                             @Valid PageRequest request) {
    return promotionService.getApplicationPromotionPage(applicationId, request.pageIndex(), request.pageSize())
        .map(body -> Response.ok(body).build());
  }
}
