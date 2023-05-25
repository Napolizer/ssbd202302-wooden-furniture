package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountSearchSettings;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Administrator;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Client;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Company;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Employee;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Mode;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.SalesRep;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TimeZone;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountSearchSettingsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangeModeDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.FullNameDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.GithubAccountInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.GoogleAccountInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

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
        .timeZone(mapStringToTimeZone(accountRegisterDto.getTimeZone()))
        .person(person).build();
    client.setAccount(account);
    account.getAccessLevels().add(client);

    return account;
  }

  public static TimeZone mapStringToTimeZone(String timeZone) {
    try {
      return TimeZone.valueOf(timeZone);
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createInvalidTimeZoneException();
    }
  }

  public static Account mapAccountCreateDtoToAccount(AccountCreateDto accountCreateDto) {
    Account account = mapAccountRegisterDtoToAccount(accountCreateDto);
    AccessLevel accessLevel = mapAccessLevelDtoToAccessLevel(accountCreateDto.getAccessLevel());

    if (accessLevel.getRoleName().equals(CLIENT)) {
      return account;
    }
    account.getAccessLevels().clear();
    accessLevel.setAccount(account);
    account.getAccessLevels().add(accessLevel);

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

  public static EditPersonInfoDto mapAccountToEditPersonInfoDto(Account account) {
    Person person = account.getPerson();
    Address address = person.getAddress();

    return EditPersonInfoDto.builder()
            .firstName(person.getFirstName())
            .lastName(person.getLastName())
            .country(address.getCountry())
            .city(address.getCity())
            .street(address.getStreet())
            .postalCode(address.getPostalCode())
            .streetNumber(address.getStreetNumber())
            .hash(CryptHashUtils.hashVersion(account.getSumOfVersions()))
            .build();
  }

  public static AccessLevel mapAccessLevelDtoToAccessLevel(AccessLevelDto accessLevelDto) {
    switch (accessLevelDto.getName().toLowerCase()) {
      case CLIENT -> {
        return new Client();
      }
      case ADMINISTRATOR -> {
        return new Administrator();
      }
      case EMPLOYEE -> {
        return new Employee();
      }
      case SALES_REP -> {
        return new SalesRep();
      }
      default -> throw ApplicationExceptionFactory.createInvalidAccessLevelException();
    }
  }

  public static GoogleAccountInfoDto mapAccountToGoogleAccountInfoDto(Account account) {
    String login = account.getEmail().split("@")[0];
    String loginCapitalized = login.substring(0, 1).toUpperCase() + login.substring(1);
    return GoogleAccountInfoDto.builder().login(loginCapitalized)
            .firstName(account.getPerson().getFirstName())
            .lastName(account.getPerson().getLastName())
            .locale(account.getLocale())
            .idToken(account.getPassword())
            .email(account.getEmail()).build();
  }

  public static GithubAccountInfoDto mapAccountToGithubAccountInfoDto(Account account) {
    return GithubAccountInfoDto.builder()
        .login(account.getLogin())
        .email(account.getEmail())
        .build();
  }

  public static FullNameDto mapAccountNameToFullNameDto(Account account) {
    return new FullNameDto(account.getPerson().getFirstName() + " " + account.getPerson().getLastName());
  }

  public static AccountSearchSettings mapAccountSearchSettingsDtoToAccountSearchSettings(
      AccountSearchSettingsDto accountSearchSettingsDto) {
    return AccountSearchSettings.builder()
        .searchPage(accountSearchSettingsDto.getSearchPage())
        .displayedAccounts(accountSearchSettingsDto.getDisplayedAccounts())
        .searchKeyword(accountSearchSettingsDto.getSearchKeyword())
        .sortBy(accountSearchSettingsDto.getSortBy())
        .sortAscending(accountSearchSettingsDto.getSortAscending())
        .build();
  }

  public static AccountSearchSettingsDto mapAccountSearchSettingsToAccountSearchSettingsDto(AccountSearchSettings accountSearchSettings) {
    return AccountSearchSettingsDto.builder()
            .searchPage(accountSearchSettings.getSearchPage())
            .displayedAccounts(accountSearchSettings.getDisplayedAccounts())
            .searchKeyword(accountSearchSettings.getSearchKeyword())
            .sortBy(accountSearchSettings.getSortBy())
            .sortAscending(accountSearchSettings.getSortAscending())
            .build();
  }

  public static Mode mapChangeModeDtoToMode(ChangeModeDto changeModeDto) {
    switch (changeModeDto.getMode().toLowerCase()) {
      case "light" -> {
        return Mode.LIGHT;
      }
      case "dark" -> {
        return Mode.DARK;
      }
      default -> throw ApplicationExceptionFactory.createInvalidModeException();
    }
  }
}
