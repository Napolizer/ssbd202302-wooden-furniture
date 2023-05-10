package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class AdministratorAccessLevelAlreadyAssignedException extends BaseWebApplicationException {
  public AdministratorAccessLevelAlreadyAssignedException() {
    super(MessageUtil.MessageKey.ACCOUNT_ADMINISTRATOR_ACCESS_LEVEL_ALREADY_ASSIGNED,
                Response.Status.BAD_REQUEST);
  }
}
