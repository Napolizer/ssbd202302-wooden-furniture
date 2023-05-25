package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;

@Local
public interface AddressFacadeOperations {

  Optional<Address> find(Long id);

  List<Address> findAll();

  List<Address> findAllPresent();

  List<Address> findAllArchived();

  List<Address> findAllByCountry(String country);

  List<Address> findAllByCity(String city);

  List<Address> findAllByStreet(String street);

  List<Address> findAllByPostalCode(String postalCode);

  List<Address> findAllByStreetNumber(Integer streetNumber);

}
