package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateDto;

@Local
public interface RateEndpointOperations {

  void delete(Long id, String login);

  RateDto create(String login, RateCreateDto entity);

  RateDto archive(Long id, RateDto entity);

  RateDto update(Long id, RateDto entity);

  Optional<RateDto> find(Long id);

  List<RateDto> findAll();

  List<RateDto> findAllPresent();

  List<RateDto> findAllArchived();

  List<RateDto> findAllByValue(Integer value);

  List<RateDto> findAllByPersonId(Long personId);

}
