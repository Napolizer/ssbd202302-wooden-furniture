package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class ClientAndSalesRepAccessLevelsConflictException extends BaseWebApplicationException {
  public ClientAndSalesRepAccessLevelsConflictException() {
    super(MessageUtil.MessageKey.ACCOUNT_CLIENT_AND_SALES_REP_ACCESS_LEVEL_CONFLICT, Response.Status.BAD_REQUEST);
  }
}
