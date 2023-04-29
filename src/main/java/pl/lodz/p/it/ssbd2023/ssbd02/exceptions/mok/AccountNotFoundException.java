package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class AccountNotFoundException extends BaseWebApplicationException {
  public AccountNotFoundException() {
    super(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND, Response.Status.NOT_FOUND);
  }
}
