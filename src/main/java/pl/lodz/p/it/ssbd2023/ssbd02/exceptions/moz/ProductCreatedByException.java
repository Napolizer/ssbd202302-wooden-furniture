package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class ProductCreatedByException extends BaseWebApplicationException {
  public ProductCreatedByException() {
    super(MessageUtil.MessageKey.PRODUCT_CREATED_BY, Response.Status.CONFLICT);
  }
}
