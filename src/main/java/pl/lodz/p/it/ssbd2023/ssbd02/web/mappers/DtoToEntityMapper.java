package pl.lodz.p.it.ssbd2023.ssbd02.web.mappers;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.InvalidAccessLevelException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.*;

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

    public static Person mapEditPersonInfoAsAdminDtoToPerson(EditPersonInfoAsAdminDto editPersonInfoAsAdminDto) {
        return Person.builder()
                .firstName(editPersonInfoAsAdminDto.getFirstName())
                .lastName(editPersonInfoAsAdminDto.getLastName())
                .address(Address.builder()
                        .country(editPersonInfoAsAdminDto.getCountry())
                        .city(editPersonInfoAsAdminDto.getCity())
                        .street(editPersonInfoAsAdminDto.getStreet())
                        .postalCode(editPersonInfoAsAdminDto.getPostalCode())
                        .streetNumber(editPersonInfoAsAdminDto.getStreetNumber()).build())
                .account(Account.builder()
                        .email(editPersonInfoAsAdminDto.getEmail())
                        .build())
                .build();
    }

    public static Person mapEditPersonInfoDtoToPerson(EditPersonInfoDto editPersonInfoDto) {
        return Person.builder()
                .firstName(editPersonInfoDto.getFirstName())
                .lastName(editPersonInfoDto.getLastName())
                .address(Address.builder()
                        .country(editPersonInfoDto.getCountry())
                        .city(editPersonInfoDto.getCity())
                        .street(editPersonInfoDto.getStreet())
                        .postalCode(editPersonInfoDto.getPostalCode())
                        .streetNumber(editPersonInfoDto.getStreetNumber()).build())
                .build();
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
