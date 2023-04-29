package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class UnknownErrorException extends BaseWebApplicationException {
  public UnknownErrorException(Throwable cause) {
    super(MessageUtil.MessageKey.ERROR_UNKNOWN_EXCEPTION, cause, Response.Status.BAD_REQUEST);
  }
}
