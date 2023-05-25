package pl.lodz.p.it.ssbd2023.ssbd02.exceptions;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class ApplicationTransactionRollbackException extends BaseWebApplicationException {

  public ApplicationTransactionRollbackException() {
    super(MessageUtil.MessageKey.TRANSACTION_ROLLBACK, Response.Status.REQUEST_TIMEOUT);
  }
}
