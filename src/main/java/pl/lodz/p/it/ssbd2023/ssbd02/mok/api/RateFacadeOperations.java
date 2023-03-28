package pl.lodz.p.it.ssbd2023.ssbd02.mok.api;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;

import java.util.List;

public interface RateFacadeOperations {
    List<Rate> findAllByValue(Integer value);
    List<Rate> findAllByPersonId(Long personId);
}
