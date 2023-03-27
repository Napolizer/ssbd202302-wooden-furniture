package pl.lodz.p.it.ssbd2023.ssbd02.mok.api;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;

import java.util.List;

public interface AddressFacadeOperations {
    List<Address> findAllByCountry(String country);
    List<Address> findAllByCity(String city);
    List<Address> findAllByStreet(String street);
    List<Address> findAllByPostalCode(String postalCode);
    List<Address> findAllByStreetNumber(String streetNumber);
}
