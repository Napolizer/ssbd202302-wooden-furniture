package pl.lodz.p.it.ssbd2023.ssbd02.web;

public class InitData {

  public static String accountToRegister = """
          {
              "firstName": "Thomas",
              "lastName": "Coe",
              "country": "Germany",
              "city": "Berlin",
              "street": "Street",
              "streetNumber": 34,
              "postalCode": "93-540",
              "password": "Password123!",
              "locale": "pl",
              "login": "Register123",
              "email": "register123@example.com"
          }
      """;

  public static String accountToRegisterWithCompany = """
          {
              "firstName": "John",
              "lastName": "Coe",
              "country": "Germany",
              "city": "Berlin",
              "street": "Street",
              "streetNumber": 34,
              "postalCode": "93-540",
              "password": "Password123!",
              "locale": "pl",
              "login": "Company123",
              "email": "company123@example.com",
              "nip": 1231231231,
              "companyName": "Company"
          }
      """;

  public static String accountToRegisterWithSameCompanyNip = """
          {
              "firstName": "John",
              "lastName": "Coe",
              "country": "Germany",
              "city": "Berlin",
              "street": "Street",
              "streetNumber": 34,
              "postalCode": "93-540",
              "password": "Password123!",
              "locale": "pl",
              "login": "Company1231",
              "email": "company1231@example.com",
              "nip": 1231231231,
              "companyName": "Company"
          }
      """;

  public static String accountToRegisterWithExistingEmail = """
          {
              "firstName": "John",
              "lastName": "Coe",
              "country": "Germany",
              "city": "Berlin",
              "street": "Street",
              "streetNumber": 34,
              "postalCode": "93-540",
              "password": "Password123!",
              "locale": "pl",
              "login": "Company999",
              "email": "register123@example.com",
              "nip": 9999999999,
              "companyName": "Company"
          }
      """;

  public static String accountToRegisterWithExistingLogin = """
          {
              "firstName": "Thomas",
              "lastName": "Coe",
              "country": "Germany",
              "city": "Berlin",
              "street": "Street",
              "streetNumber": 34,
              "postalCode": "93-540",
              "password": "Password123!",
              "locale": "pl",
              "login": "Register123",
              "email": "register12122@example.com"
          }
      """;
  public static String accountToEditAccessLevelsJson = """
          {
              "firstName": "Thomas",
              "lastName": "Coe",
              "country": "Germany",
              "city": "Berlin",
              "street": "Street",
              "streetNumber": 34,
              "postalCode": "93-540",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel": [
                  {
                      "name": "Employee"
                  }
              ],
              "login": "accounttoeditals",
              "email": "toeditaccesslevel@example.com"
          }
      """;


  public static String accountToEditJson = """
          {
              "firstName": "John",
              "lastName": "Boe",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Employee"
                },
              "login": "accounttoedit123",
              "email": "toedit@example.com"
          }
      """;

  public static String activeAccountJson = """
          {
              "firstName": "John",
              "lastName": "Boe",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Employee"
                },
              "login": "active123",
              "email": "active123@example.com"
          }
      """;

  public static String blockedAccountJson = """
          {
              "firstName": "John",
              "lastName": "Boe",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Employee"
                },
              "login": "blocked123",
              "email": "blocked123@example.com",
              "accountState": "BLOCKED"
          }
      """;

  public static String notVerifiedAccountJson = """
          {
              "firstName": "John",
              "lastName": "Boe",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Employee"
                },
              "login": "notverified123",
              "email": "notverified123@example.com",
              "accountState": "NOT_VERIFIED"
          }
      """;

  public static String inactiveAccountJson = """
          {
              "firstName": "John",
              "lastName": "Boe",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Employee"
                },
              "login": "inactive123",
              "email": "inactive123@example.com",
              "accountState": "INACTIVE"
          }
      """;

  public static String sameLoginAccountJson = """
          {
              "firstName": "John",
              "lastName": "Boe",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Employee"
                },
              "login": "active123",
              "email": "test123@example.com",
              "accountState": "ACTIVE"
          }
      """;

  public static String sameEmailAccountJson = """
          {
              "firstName": "John",
              "lastName": "Boe",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Employee"
                },
              "login": "test123",
              "email": "active123@example.com",
              "accountState": "ACTIVE"
          }
      """;

  public static String invalidAccessLevelAccountJson = """
          {
              "firstName": "John",
              "lastName": "Boe",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Invalid"
                },
              "login": "test123",
              "email": "active123@example.com",
              "accountState": "ACTIVE"
          }
      """;
  public static String editedAccountExampleJson = """
      {
        "firstName": "Adam",
        "lastName": "Doe",
        "country": "USA",
        "city": "Warsaw",
        "street": "Wladyslawa",
        "postalCode": "95-200",
        "streetNumber": 20,
        "hash": "$hash"
      }
      """;

  public static String editedAccountAsAdminExampleJson = """
      {
        "firstName": "John",
        "lastName": "Doe",
        "country": "United States",
        "city": "New York",
        "street": "Broadway",
        "postalCode": "10-001",
        "streetNumber": 123,
        "hash": "$hash"
      }
      """;

}
