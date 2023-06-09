package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper.RateMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateInputDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.RateEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.RateServiceOperations;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
public class RateEndpoint implements RateEndpointOperations {

  @Inject
  private RateServiceOperations rateService;

  @Override
  @RolesAllowed(CLIENT)
  public void delete(Long id, String login) {
    rateService.delete(id, login);
  }

  @Override
  @RolesAllowed(CLIENT)
  public RateDto create(String login, RateInputDto entity) {
    Rate rate = rateService.create(login, entity.getRate(), entity.getProductId());
    return RateMapper.matToRateDto(rate);
  }

  @Override
  public RateDto archive(Long id, RateDto entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  @RolesAllowed(CLIENT)
  public RateDto update(Long id, RateInputDto entity, String login) {
    Rate updatedRate = rateService.update(id, entity.getRate(), login);
    return RateMapper.matToRateDto(updatedRate);
  }

  @Override
  public Optional<RateDto> find(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<RateDto> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<RateDto> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<RateDto> findAllArchived() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<RateDto> findAllByValue(Integer value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<RateDto> findAllByPersonId(Long personId) {
    throw new UnsupportedOperationException();
  }
}
