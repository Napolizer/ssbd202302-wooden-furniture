package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;


@Local
public interface RateServiceOperations {

  void delete(Long id);

  Rate create(Rate entity);

  Rate archive(Long id, Rate entity);

  Rate update(Long id, Rate entity);

  Optional<Rate> find(Long id);

  List<Rate> findAll();

  List<Rate> findAllPresent();

  List<Rate> findAllArchived();

  List<Rate> findAllByValue(Integer value);

  List<Rate> findAllByPersonId(Long personId);
}