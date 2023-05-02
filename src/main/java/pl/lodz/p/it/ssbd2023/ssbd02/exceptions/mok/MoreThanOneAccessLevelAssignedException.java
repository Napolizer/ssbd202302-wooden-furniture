package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class MoreThanOneAccessLevelAssignedException extends BaseWebApplicationException {
  public MoreThanOneAccessLevelAssignedException() {
    super(MessageUtil.MessageKey.ACCOUNT_MORE_THAN_ONE_ACCESS_LEVEL, Response.Status.BAD_REQUEST);
  }
}
