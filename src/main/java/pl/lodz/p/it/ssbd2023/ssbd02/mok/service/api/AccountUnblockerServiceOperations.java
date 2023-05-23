package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api;

import jakarta.ejb.Local;
import java.util.Date;

@Local
public interface AccountUnblockerServiceOperations {

  void unblockAccountAfterTimeout(Long accountId, Date operationTime);
}
