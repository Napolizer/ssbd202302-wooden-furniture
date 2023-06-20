package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class ProductPriceChangedException extends BaseWebApplicationException {
  public ProductPriceChangedException() {
    super(MessageUtil.MessageKey.PRODUCT_PRICE_CHANGED, Response.Status.CONFLICT);
  }
}
