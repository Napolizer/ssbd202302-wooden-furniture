package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;

public class RateNotFoundException extends BaseWebApplicationException {
  public RateNotFoundException(String message, Response.Status statusCode) {
    super(message, statusCode);
  }
}
