package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class ParentCategoryNotAllowedException extends BaseWebApplicationException {
  public ParentCategoryNotAllowedException() {
    super(MessageUtil.MessageKey.PARENT_CATEGORY_NOT_ALLOWED, Response.Status.BAD_REQUEST);
  }
}
