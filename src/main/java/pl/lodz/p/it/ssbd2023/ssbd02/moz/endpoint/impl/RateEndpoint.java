package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.RateEndpointOperations;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
public class RateEndpoint implements RateEndpointOperations {
  @Override
  public void delete(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public RateDto create(RateDto entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public RateDto archive(RateDto entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public RateDto update(RateDto entity) {
    throw new UnsupportedOperationException();
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
