package org.task.pilot.rest.resource;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.task.pilot.domain.command.ApprovePromotion;
import org.task.pilot.domain.command.CancelPromotion;
import org.task.pilot.domain.command.CompletePromotion;
import org.task.pilot.domain.command.RequestPromotion;
import org.task.pilot.domain.command.RollbackPromotion;
import org.task.pilot.domain.command.StartDeployment;
import org.task.pilot.handler.CommandBus;
import org.task.pilot.rest.dto.ApprovePromotionRequest;
import org.task.pilot.rest.dto.CancelPromotionRequest;
import org.task.pilot.rest.dto.RequestPromotionRequest;
import org.task.pilot.rest.dto.RollbackPromotionRequest;

import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("/promotions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PromotionResource {

  private final CommandBus bus;

  public PromotionResource(CommandBus bus) {
    this.bus = bus;
  }

  @POST
  public Uni<Response> requestPromotion(@Valid RequestPromotionRequest request) {
    var command = new RequestPromotion(request.applicationId(), request.applicationVersion(),
        request.targetEnvironment(), request.requestedBy());

    return bus.dispatch(command)
        .map(IdResponse::new)
        .map(body -> Response.status(CREATED).entity(body).build());
  }

  @POST
  @Path("/{id}/approve")
  public Uni<Response> approve(@PathParam("id") UUID promotionId,
                               @Valid ApprovePromotionRequest body) {
    return bus.dispatch(new ApprovePromotion(promotionId, body.approverId()))
        .map(ignored -> Response.accepted().build());
  }

  @POST
  @Path("/{id}/start-deployment")
  public Uni<Response> startDeployment(@PathParam("id") UUID promotionId) {
    return bus.dispatch(new StartDeployment(promotionId))
        .map(ignored -> Response.accepted().build());
  }

  @POST
  @Path("/{id}/complete")
  public Uni<Response> complete(@PathParam("id") UUID promotionId) {
    return bus.dispatch(new CompletePromotion(promotionId))
        .map(ignored -> Response.accepted().build());
  }

  @POST
  @Path("/{id}/rollback")
  public Uni<Response> rollback(@PathParam("id") UUID promotionId,
                                @Valid RollbackPromotionRequest body) {
    return bus.dispatch(new RollbackPromotion(promotionId, body.reason()))
        .map(ignored -> Response.accepted().build());
  }

  @POST
  @Path("/{id}/cancel")
  public Uni<Response> cancel(@PathParam("id") UUID promotionId,
                              @Valid CancelPromotionRequest body) {
    return bus.dispatch(new CancelPromotion(promotionId, body.cancelledBy(), body.reason()))
        .map(ignored -> Response.accepted().build());
  }

  public record IdResponse(UUID id) {
  }
}
