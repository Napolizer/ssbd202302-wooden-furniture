package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class ExpiredLinkException extends BaseWebApplicationException {
  public ExpiredLinkException(Throwable cause) {
    super(MessageUtil.MessageKey.ERROR_EXPIRED_LINK, cause, Response.Status.GONE);
  }
}
