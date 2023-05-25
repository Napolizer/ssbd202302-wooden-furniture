package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.AddressFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.AddressServiceOperations;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AddressService implements AddressServiceOperations {

  @Inject
  private AddressFacadeOperations addressFacade;

  @Override
  public Optional<Address> find(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Address> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Address> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Address> findAllArchived() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Address> findAllByCountry(String country) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Address> findAllByCity(String city) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Address> findAllByStreet(String street) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Address> findAllByPostalCode(String postalCode) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Address> findAllByStreetNumber(Integer streetNumber) {
    throw new UnsupportedOperationException();
  }
}
