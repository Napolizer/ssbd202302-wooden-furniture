package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.Facade;

import java.util.List;

public interface RateFacadeOperations extends Facade<Rate> {
    List<Rate> findAllByValue(Integer value);
    List<Rate> findAllByPersonId(Long personId);
}
