package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class IllegalProductDeArchiveException extends BaseWebApplicationException {
  public IllegalProductDeArchiveException() {
    super(MessageUtil.MessageKey.PRODUCT_CHANGE_STATE_DEARCHIVE, Response.Status.BAD_REQUEST);
  }
}
