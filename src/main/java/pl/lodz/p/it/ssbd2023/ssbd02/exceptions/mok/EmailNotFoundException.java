package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class EmailNotFoundException extends BaseWebApplicationException {
  public EmailNotFoundException() {
    super(MessageUtil.MessageKey.ACCOUNT_EMAIL_DOES_NOT_EXIST, Response.Status.NOT_FOUND);
  }
}
