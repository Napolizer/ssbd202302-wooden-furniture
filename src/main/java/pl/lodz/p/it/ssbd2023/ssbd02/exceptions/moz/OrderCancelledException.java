package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class OrderCancelledException extends BaseWebApplicationException {
  public OrderCancelledException() {
    super(MessageUtil.MessageKey.ORDER_CANCELLED, Response.Status.BAD_REQUEST);
  }
}
