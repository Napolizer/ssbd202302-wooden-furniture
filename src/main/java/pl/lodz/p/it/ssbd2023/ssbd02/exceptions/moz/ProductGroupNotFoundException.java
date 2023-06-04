package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class ProductGroupNotFoundException extends BaseWebApplicationException {
  public ProductGroupNotFoundException() {
    super(MessageUtil.MessageKey.PRODUCT_GROUP_NOT_FOUND, Response.Status.NOT_FOUND);
  }
}
