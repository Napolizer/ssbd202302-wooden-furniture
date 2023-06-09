package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class ProductAlreadyExistsException extends BaseWebApplicationException {
  public ProductAlreadyExistsException() {
    super(MessageUtil.MessageKey.PRODUCT_ALREADY_EXITS, Response.Status.CONFLICT);
  }
}
