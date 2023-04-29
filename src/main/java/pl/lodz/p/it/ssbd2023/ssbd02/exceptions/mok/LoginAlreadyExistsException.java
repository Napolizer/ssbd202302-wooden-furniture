package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class LoginAlreadyExistsException extends BaseWebApplicationException {
  public LoginAlreadyExistsException(Throwable cause) {
    super(MessageUtil.MessageKey.ACCOUNT_LOGIN_ALREADY_EXISTS, cause, Response.Status.CONFLICT);
  }
}
