package pl.lodz.p.it.ssbd2023.ssbd02.moz.api;

import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.facade.Facade;

public interface RateFacadeOperations extends Facade<Rate> {
  List<Rate> findAllByValue(Integer value);

  List<Rate> findAllByPersonId(Long personId);
}
