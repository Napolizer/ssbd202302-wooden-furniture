package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.RateFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.RateServiceOperations;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RateService implements RateServiceOperations {

  @Inject
  private RateFacadeOperations rateFacade;

  @Override
  public void delete(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Rate create(Rate entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Rate archive(Long id, Rate entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Rate update(Long id, Rate entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<Rate> find(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Rate> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Rate> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Rate> findAllArchived() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Rate> findAllByValue(Integer value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Rate> findAllByPersonId(Long personId) {
    throw new UnsupportedOperationException();
  }
}
