package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class CategoryNotFoundException extends BaseWebApplicationException {
  public CategoryNotFoundException() {
    super(MessageUtil.MessageKey.CATEGORY_NOT_FOUND, Response.Status.NOT_FOUND);
  }
}
