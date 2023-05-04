package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;

public class ExpiredLinkException extends BaseWebApplicationException {
  public ExpiredLinkException(String message) {
    super(message, Response.Status.GONE);
  }
}
