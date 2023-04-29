package pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;

public class ApplicationOptimisticLockException extends BaseWebApplicationException {
  public ApplicationOptimisticLockException() {
    super(MessageUtil.MessageKey.ERROR_OPTIMISTIC_LOCK, Response.Status.CONFLICT);
  }
}
