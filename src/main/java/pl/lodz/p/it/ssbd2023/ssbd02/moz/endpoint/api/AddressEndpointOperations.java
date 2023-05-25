package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.address.AddressDto;

@Local
public interface AddressEndpointOperations {

  Optional<AddressDto> find(Long id);

  List<AddressDto> findAll();

  List<AddressDto> findAllPresent();

  List<AddressDto> findAllArchived();

  List<AddressDto> findAllByCountry(String country);

  List<AddressDto> findAllByCity(String city);

  List<AddressDto> findAllByStreet(String street);

  List<AddressDto> findAllByPostalCode(String postalCode);

  List<AddressDto> findAllByStreetNumber(Integer streetNumber);
}
