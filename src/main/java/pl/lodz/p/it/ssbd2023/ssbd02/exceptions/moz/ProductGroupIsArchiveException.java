package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class ProductGroupIsArchiveException extends BaseWebApplicationException {
  public ProductGroupIsArchiveException() {
    super(MessageUtil.MessageKey.PRODUCT_GROUP_IS_ARCHIVE, Response.Status.BAD_REQUEST);
  }
}
