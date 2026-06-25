package org.task.pilot.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.Response.Status.*;

public final class Thrower {

  public static WebApplicationException webThrow(String message) {
    return toException(message, BAD_REQUEST);
  }

  public static WebApplicationException webForbidden(String message) {
    return toException(message, FORBIDDEN);
  }

  public static WebApplicationException webNotFound(String message) {
    return toException(message, NOT_FOUND);
  }

  private static WebApplicationException toException(String message, Response.Status status) {
    var body = new ExceptionBody(message);
    return new WebApplicationException(Response.status(status).entity(body).build());
  }

  record ExceptionBody(String message) {
  }
}
