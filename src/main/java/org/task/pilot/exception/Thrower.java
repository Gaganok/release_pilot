package org.task.pilot.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public final class Thrower {

  public static WebApplicationException webThrow(String message) {
    var body = new ExceptionBody(message);
    return new WebApplicationException(Response.status(BAD_REQUEST).entity(body).build());
  }

  record ExceptionBody(String message) {
  }
}
