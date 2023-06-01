package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper;

import jakarta.ejb.Stateful;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AddressDto;

@Stateful
public class AddressMapper {
  public AddressDto mapToAddressDto(Address address) {
    return AddressDto.builder()
        .country(address.getCountry())
        .city(address.getCity())
        .street(address.getStreet())
        .postalCode(address.getPostalCode())
        .streetNumber(address.getStreetNumber())
        .build();
  }

  public Address mapToAddress(AddressDto addressDto) {
    return Address.builder()
        .country(addressDto.getCountry())
        .city(addressDto.getCity())
        .street(addressDto.getStreet())
        .postalCode(addressDto.getPostalCode())
        .streetNumber(addressDto.getStreetNumber())
        .build();
  }
}
