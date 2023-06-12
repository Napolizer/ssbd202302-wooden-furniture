package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class InvalidProductAmountException extends BaseWebApplicationException {
  public InvalidProductAmountException() {
    super(MessageUtil.MessageKey.INVALID_PRODUCT_AMOUNT, Response.Status.BAD_REQUEST);
  }
}
