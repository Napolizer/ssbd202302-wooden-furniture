package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api;

import jakarta.ejb.Local;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.facade.Facade;

@Local
public interface RateFacadeOperations extends Facade<Rate> {

  void delete(Rate rate);

  List<Rate> findAllByValue(Integer value);

  List<Rate> findAllByPersonId(Long personId);
}
