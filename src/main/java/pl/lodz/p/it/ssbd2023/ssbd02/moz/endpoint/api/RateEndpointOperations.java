package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateInputDto;

@Local
public interface RateEndpointOperations {

  void delete(String login, Long productGroupId);

  RateDto create(String login, RateInputDto entity);

  RateDto update(String login, RateInputDto entity);


}
