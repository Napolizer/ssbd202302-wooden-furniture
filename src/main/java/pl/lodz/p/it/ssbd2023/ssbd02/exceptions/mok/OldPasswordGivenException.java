package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class OldPasswordGivenException extends BaseWebApplicationException {
  public OldPasswordGivenException() {
    super(MessageUtil.MessageKey.ACCOUNT_OLD_PASSWORD, Response.Status.CONFLICT);
  }
}

