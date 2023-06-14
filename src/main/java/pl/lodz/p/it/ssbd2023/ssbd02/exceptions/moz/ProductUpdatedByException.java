package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class ProductUpdatedByException extends BaseWebApplicationException {
  public ProductUpdatedByException() {
    super(MessageUtil.MessageKey.PRODUCT_UPDATED_BY, Response.Status.CONFLICT);
  }
}
