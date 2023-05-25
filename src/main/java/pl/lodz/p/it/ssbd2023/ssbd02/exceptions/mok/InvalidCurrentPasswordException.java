package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;

public class InvalidCurrentPasswordException extends BaseWebApplicationException {
  public InvalidCurrentPasswordException(String message, Response.Status statusCode) {
    super(message, statusCode);
  }
}
