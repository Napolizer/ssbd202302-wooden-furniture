package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.address.AddressDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.AddressEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.AddressServiceOperations;


@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
public class AddressEndpoint implements AddressEndpointOperations {

  @Inject
  private AddressServiceOperations addressService;

  @Override
  public Optional<AddressDto> find(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<AddressDto> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<AddressDto> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<AddressDto> findAllArchived() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<AddressDto> findAllByCountry(String country) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<AddressDto> findAllByCity(String city) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<AddressDto> findAllByStreet(String street) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<AddressDto> findAllByPostalCode(String postalCode) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<AddressDto> findAllByStreetNumber(Integer streetNumber) {
    throw new UnsupportedOperationException();
  }
}
