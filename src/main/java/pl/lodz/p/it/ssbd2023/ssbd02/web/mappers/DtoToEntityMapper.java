package pl.lodz.p.it.ssbd2023.ssbd02.web.mappers;

import java.util.ArrayList;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Administrator;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Client;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Company;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Employee;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.SalesRep;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;

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

    String nip = accountRegisterDto.getNip();
    String companyName = accountRegisterDto.getCompanyName();
    Client client = new Client();
    if (nip != null && companyName != null) {
      client.setCompany(Company.builder().nip(nip).client(client).companyName(companyName).build());
    }

    Account account = Account.builder()
        .login(accountRegisterDto.getLogin())
        .email(accountRegisterDto.getEmail())
        .password(accountRegisterDto.getPassword())
        .locale(accountRegisterDto.getLocale())
        .person(person).build();
    client.setAccount(account);
    account.getAccessLevels().add(client);

    return account;
  }


  public static Account mapAccountCreateDtoToAccount(AccountCreateDto accountCreateDto) {
    Account account = mapAccountRegisterDtoToAccount(accountCreateDto);
    account.setAccountState(accountCreateDto.getAccountState());
    List<AccessLevel> accessLevels = new ArrayList<>();

    for (AccessLevelDto accessLevel : accountCreateDto.getAccessLevels()) {
      AccessLevel mapped = mapAccessLevelDtoToAccessLevel(accessLevel);
      mapped.setAccount(account);
      accessLevels.add(mapped);
    }
    account.setAccessLevels(accessLevels);

    return account;
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
