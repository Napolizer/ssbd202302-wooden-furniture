package pl.lodz.p.it.ssbd2023.ssbd02.web.mappers;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;

public final class DtoToEntityMapper {
    public static Person mapAccountRegisterDtoToPerson(AccountRegisterDto accountRegisterDto) {
        return Person.builder()
                .firstName(accountRegisterDto.getFirstName())
                .lastName(accountRegisterDto.getLastName())
                .address(Address.builder()
                        .country(accountRegisterDto.getCountry())
                        .city(accountRegisterDto.getCity())
                        .postalCode(accountRegisterDto.getPostalCode())
                        .street(accountRegisterDto.getStreet())
                        .streetNumber(accountRegisterDto.getStreetNumber()).build())
                .account(Account.builder()
                        .login(accountRegisterDto.getLogin())
                        .email(accountRegisterDto.getEmail())
                        .password(accountRegisterDto.getPassword())
                        .locale(accountRegisterDto.getLocale()).build()).build();
    }
}
