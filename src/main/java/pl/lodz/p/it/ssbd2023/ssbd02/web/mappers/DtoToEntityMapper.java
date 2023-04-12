package pl.lodz.p.it.ssbd2023.ssbd02.web.mappers;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.InvalidAccessLevelException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;

import java.util.ArrayList;
import java.util.List;

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

    public static Person mapAccountCreateDtoToPerson(AccountCreateDto accountCreateDto) throws Exception {
        Person person = mapAccountRegisterDtoToPerson(accountCreateDto);
        person.getAccount().setAccountState(accountCreateDto.getAccountState());
        List<AccessLevel> accessLevels = new ArrayList<>();
        for (AccessLevelDto accessLevel: accountCreateDto.getAccessLevels()) {
            AccessLevel mapped = mapAccessLevelDtoToAccessLevel(accessLevel);
            accessLevels.add(mapped);
        }
        person.getAccount().setAccessLevels(accessLevels);

        return person;
    }

    public static AccessLevel mapAccessLevelDtoToAccessLevel(AccessLevelDto accessLevelDto)
            throws InvalidAccessLevelException {

        switch (accessLevelDto.getName()) {
            case "Client" -> {
                return new Client();
            }
            case "Administrator" -> {
                return new Administrator();
            }
            case "Employee" -> {
                return new Employee();
            }
            case "SalesRep" -> {
                return new SalesRep();
            }
            default -> throw new InvalidAccessLevelException();
        }
    }
}
