package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class OrderAlreadyDeliveredException extends BaseWebApplicationException {
  public OrderAlreadyDeliveredException() {
    super(MessageUtil.MessageKey.ORDER_ALREADY_DELIVERED, Response.Status.BAD_REQUEST);
  }
}
