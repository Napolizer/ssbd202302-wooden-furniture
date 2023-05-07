package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class AccountNotVerifiedException extends BaseWebApplicationException {
  public AccountNotVerifiedException() {
    super(MessageUtil.MessageKey.ACCOUNT_NOT_VERIFIED, Response.Status.BAD_REQUEST);
  }
}
