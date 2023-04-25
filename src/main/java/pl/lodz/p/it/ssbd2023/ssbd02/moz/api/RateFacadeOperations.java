package pl.lodz.p.it.ssbd2023.ssbd02.moz.api;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.facade.Facade;

import java.util.List;

public interface RateFacadeOperations extends Facade<Rate> {
    List<Rate> findAllByValue(Integer value);
    List<Rate> findAllByPersonId(Long personId);
}
