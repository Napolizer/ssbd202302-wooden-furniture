package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api;

import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.facade.Facade;

public interface AddressFacadeOperations extends Facade<Address> {
  List<Address> findAllByCountry(String country);

  List<Address> findAllByCity(String city);

  List<Address> findAllByStreet(String street);

  List<Address> findAllByPostalCode(String postalCode);

  List<Address> findAllByStreetNumber(Integer streetNumber);
}
