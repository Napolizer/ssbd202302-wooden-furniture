package pl.lodz.p.it.ssbd2023.ssbd02.exceptions;

import jakarta.ejb.ApplicationException;
import jakarta.json.Json;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationException(rollback = true)
public class BaseWebApplicationException extends WebApplicationException {
  public BaseWebApplicationException(String message, Response.Status statusCode) {
    super(
        Response.status(statusCode).entity(Json.createObjectBuilder().add("message", message).build())
            .build());
  }

  public BaseWebApplicationException(String message, Throwable cause, Response.Status statusCode) {
    super(cause,
        Response.status(statusCode).entity(Json.createObjectBuilder().add("message", message).build())
            .build());
  }

}
