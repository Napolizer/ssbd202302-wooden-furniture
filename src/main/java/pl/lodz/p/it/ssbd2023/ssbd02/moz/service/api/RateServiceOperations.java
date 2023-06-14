package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;


@Local
public interface RateServiceOperations {

  void delete(Long id, String login);

  Rate update(Long id, Integer rateNewValue, String login);

  boolean isLastTransactionRollback();
}
