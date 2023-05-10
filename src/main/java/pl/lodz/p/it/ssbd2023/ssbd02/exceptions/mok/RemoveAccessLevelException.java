package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class RemoveAccessLevelException extends BaseWebApplicationException {
  public RemoveAccessLevelException() {
    super(MessageUtil.MessageKey.ACCOUNT_REMOVE_ACCESS_LEVEL,
        Response.Status.BAD_REQUEST);
  }
}
