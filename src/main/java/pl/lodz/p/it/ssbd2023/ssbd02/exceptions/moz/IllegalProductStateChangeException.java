package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class IllegalProductStateChangeException extends BaseWebApplicationException {
  public IllegalProductStateChangeException() {
    super(MessageUtil.MessageKey.PRODUCT_CHANGE_STATE, Response.Status.BAD_REQUEST);
  }
}
