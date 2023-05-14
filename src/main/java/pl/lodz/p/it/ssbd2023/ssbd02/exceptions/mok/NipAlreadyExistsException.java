package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class NipAlreadyExistsException extends BaseWebApplicationException {
  public NipAlreadyExistsException(Throwable cause) {
    super(MessageUtil.MessageKey.COMPANY_NIP_ALREADY_EXISTS, cause, Response.Status.CONFLICT);
  }
}
