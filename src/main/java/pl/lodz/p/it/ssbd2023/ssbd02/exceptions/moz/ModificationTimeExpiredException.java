package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class ModificationTimeExpiredException extends BaseWebApplicationException {
  public ModificationTimeExpiredException() {
    super(MessageUtil.MessageKey.MODIFICATION_TIME_EXPIRED, Response.Status.BAD_REQUEST);
  }
}
