package pl.lodz.p.it.ssbd2023.ssbd02.web.mappers;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.*;

import java.util.ArrayList;
import java.util.List;

public final class DtoToEntityMapper {
    public static Account mapAccountRegisterDtoToAccount(AccountRegisterDto accountRegisterDto) {
        Address address = Address.builder()
                .country(accountRegisterDto.getCountry())
                .city(accountRegisterDto.getCity())
                .postalCode(accountRegisterDto.getPostalCode())
                .street(accountRegisterDto.getStreet())
                .streetNumber(accountRegisterDto.getStreetNumber()).build();

        Person person = Person.builder()
                .firstName(accountRegisterDto.getFirstName())
                .lastName(accountRegisterDto.getLastName())
                .address(address).build();

        return Account.builder()
                .login(accountRegisterDto.getLogin())
                .email(accountRegisterDto.getEmail())
                .password(accountRegisterDto.getPassword())
                .locale(accountRegisterDto.getLocale())
                .person(person).build();
    }


    public static Account mapAccountCreateDtoToAccount(AccountCreateDto accountCreateDto) {
        Account account = mapAccountRegisterDtoToAccount(accountCreateDto);
        account.setAccountState(accountCreateDto.getAccountState());
        List<AccessLevel> accessLevels = new ArrayList<>();

        for (AccessLevelDto accessLevel: accountCreateDto.getAccessLevels()) {
            AccessLevel mapped = mapAccessLevelDtoToAccessLevel(accessLevel);
            mapped.setAccount(account);
            accessLevels.add(mapped);
        }
        account.setAccessLevels(accessLevels);

        return account;
    }

    public static Account mapEditPersonInfoAsAdminDtoToAccount(EditPersonInfoAsAdminDto editPersonInfoAsAdminDto) {
        Address address = Address.builder()
                .country(editPersonInfoAsAdminDto.getCountry())
                .city(editPersonInfoAsAdminDto.getCity())
                .street(editPersonInfoAsAdminDto.getStreet())
                .postalCode(editPersonInfoAsAdminDto.getPostalCode())
                .streetNumber(editPersonInfoAsAdminDto.getStreetNumber()).build();

        Person person = Person.builder()
                .firstName(editPersonInfoAsAdminDto.getFirstName())
                .lastName(editPersonInfoAsAdminDto.getLastName())
                .address(address).build();

        return Account.builder()
                .email(editPersonInfoAsAdminDto.getEmail())
                .person(person)
                .build();
    }


    public static Account mapEditPersonInfoDtoToAccount(EditPersonInfoDto editPersonInfoDto) {
        Address address = Address.builder()
                .country(editPersonInfoDto.getCountry())
                .city(editPersonInfoDto.getCity())
                .street(editPersonInfoDto.getStreet())
                .postalCode(editPersonInfoDto.getPostalCode())
                .streetNumber(editPersonInfoDto.getStreetNumber()).build();

        Person person = Person.builder()
                .firstName(editPersonInfoDto.getFirstName())
                .lastName(editPersonInfoDto.getLastName())
                .address(address).build();

        return Account.builder()
                .person(person)
                .build();
    }

    public static AccessLevel mapAccessLevelDtoToAccessLevel(AccessLevelDto accessLevelDto) {
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
            default -> throw ApplicationExceptionFactory.createInvalidAccessLevelException();
        }
    }
}
