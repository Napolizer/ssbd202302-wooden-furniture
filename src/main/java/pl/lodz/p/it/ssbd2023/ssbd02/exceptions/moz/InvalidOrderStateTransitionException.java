package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class InvalidOrderStateTransitionException extends BaseWebApplicationException {
  public InvalidOrderStateTransitionException() {
    super(MessageUtil.MessageKey.INVALID_ORDER_STATE_TRANSITION, Response.Status.BAD_REQUEST);
  }
}
