package pl.lodz.p.it.ssbd2023.ssbd02.web;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.HttpHeaders;
import pl.lodz.p.it.ssbd2023.ssbd02.config.enums.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TimeZone;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.CategoryName;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.SetEmailToSendPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.TokenService;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateWithImageDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupCreateDto;

public class InitData {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final TokenService tokenService = new TokenService();

  private static String retrieveToken(UserCredentialsDto credentials) {
    return given()
            .contentType("application/json")
            .header(HttpHeaders.ACCEPT_LANGUAGE, "pl")
            .body(InitData.mapToJsonString(credentials))
            .when()
            .post("/account/login")
            .then()
            .statusCode(200)
            .contentType("application/json")
            .extract()
            .path("token");
  }

  public static String retrieveVersion(String login) {
    return given()
            .contentType("application/json")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + retrieveAdminToken())
            .header(HttpHeaders.ACCEPT_LANGUAGE, "pl")
            .get("/account/login/" + login)
            .then()
            .statusCode(200)
            .contentType("application/json")
            .extract()
            .path("hash");
  }

  public static int retrieveAccountId(String login) {
    return given()
            .header("Authorization", "Bearer " + retrieveAdminToken())
            .when()
            .get("/account/login/" + login)
            .then()
            .statusCode(200)
            .extract()
            .path("id");
  }

  public static String mapToJsonString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException();
    }
  }

  public static String retrieveAdminToken() {
    return retrieveToken(InitData.getUserCredentials("administrator", "Student123!"));
  }

  public static String retrieveEmployeeToken() {
    return retrieveToken(InitData.getUserCredentials("employee", "Student123!"));
  }

  public static String retrieveSalesRepToken() {
    return retrieveToken(InitData.getUserCredentials("salesrep", "Student123!"));
  }

  public static String retrieveClientToken() {
    return retrieveToken(InitData.getUserCredentials("client", "Student123!"));
  }

  public static String generateChangeEmailToken(String login, String newEmail) {
    Account account = Account.builder().newEmail(newEmail).login(login).build();
    return tokenService.generateTokenForEmailLink(account, TokenType.CHANGE_EMAIL);
  }

  public static String generateConfirmEmailToken(String login) {
    Account account = Account.builder().login(login).build();
    return tokenService.generateTokenForEmailLink(account, TokenType.ACCOUNT_CONFIRMATION);
  }

  public static UserCredentialsDto getUserCredentials(String login, String password) {
    return UserCredentialsDto.builder()
            .login(login)
            .password(password)
            .build();
  }

  public static SetEmailToSendPasswordDto getEmailToChange(String newEmail) {
    return new SetEmailToSendPasswordDto(newEmail);
  }

  public static AccountRegisterDto getAccountToRegister() {
    return AccountRegisterDto.builder()
            .firstName("Thomas")
            .lastName("Coe")
            .country("Germany")
            .city("Berlin")
            .street("Street")
            .streetNumber(34)
            .postalCode("93-540")
            .password("Password123!")
            .locale("pl")
            .login("Register123")
            .email("register123@example.com")
            .timeZone(TimeZone.EUROPE_WARSAW.name())
            .build();
  }

  public static AccountRegisterDto getAccountToRegisterForMultipleThreads() {
    return AccountRegisterDto.builder()
            .firstName("Thomas")
            .lastName("Coe")
            .country("Germany")
            .city("Berlin")
            .street("Street")
            .streetNumber(34)
            .postalCode("93-540")
            .password("Password123!")
            .locale("pl")
            .login("Register123MT")
            .email("register123MT@example.com")
            .timeZone(TimeZone.EUROPE_WARSAW.name())
            .build();
  }

  public static AccountRegisterDto getAccountWithCompany() {
    return AccountRegisterDto.builder()
            .firstName("John")
            .lastName("Coe")
            .country("Germany")
            .city("Berlin")
            .street("Street")
            .streetNumber(34)
            .postalCode("93-540")
            .password("Password123!")
            .locale("pl")
            .login("Company123")
            .email("company123@example.com")
            .nip("1111111111")
            .companyName("New Company")
            .timeZone(TimeZone.EUROPE_WARSAW.name())
            .build();
  }

  public static AccountCreateDto getAccountToCreate() {
    return AccountCreateDto.builder()
            .firstName("John")
            .lastName("Boe")
            .country("Poland")
            .city("Lodz")
            .street("Karpacka")
            .streetNumber(55)
            .postalCode("93-539")
            .password("Password123!")
            .locale("pl")
            .accessLevel(new AccessLevelDto("administrator"))
            .login("active123")
            .email("active123@example.com")
            .nip("1111111111")
            .companyName("Example Company")
            .timeZone(TimeZone.EUROPE_WARSAW.name())
            .build();
  }

  public static EditPersonInfoDto getEditedAccount(String login) {
    return EditPersonInfoDto.builder()
            .firstName("John")
            .lastName("Coe")
            .country("Poland")
            .city("Lodz")
            .street("Karpacka")
            .streetNumber(55)
            .postalCode("93-539")
            .hash(retrieveVersion(login))
            .build();
  }

  public static ProductCreateWithImageDto getProductToCreate() {
    return ProductCreateWithImageDto.builder()
            .productGroupId(1L)
            .amount(10)
            .color("BROWN")
            .price(4999.99)
            .weight(9.5)
            .weightInPackage(10.5)
            .furnitureDepth(150)
            .furnitureHeight(150)
            .furnitureWidth(150)
            .packageDepth(150)
            .packageWidth(150)
            .packageHeight(150)
            .woodType("BIRCH")
            .imageProductId(1L)
            .build();
  }

  public static ProductGroupCreateDto getProductGroupToCreate() {
    return ProductGroupCreateDto.builder()
            .name("Test product group")
            .categoryName(CategoryName.DESK.name())
            .build();
  }

  public static String changePasswordJson = """
      {
        "currentPassword": "Student123!",
        "password": "Password123!"
      }
      """;

  public static String accountToChangePasswordJson = """
          {
              "firstName": "John",
              "lastName": "Boe",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Student123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Employee"
                },
              "login": "JohnChangePassword",
              "email": "wantsToChangePassword@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String blockedAccountToActivateJson = """
          {
              "firstName": "Thomas",
              "lastName": "Blocked",
              "country": "Germany",
              "city": "Berlin",
              "street": "Street",
              "streetNumber": 34,
              "postalCode": "93-540",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel": [
                  {
                      "name": "Client"
                  }
              ],
              "login": "blockedThomas",
              "email": "blockedToActivate@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String blockAccountAfterFailedAttempts  = """
          {
              "firstName": "Thomas",
              "lastName": "Blocked",
              "country": "Germany",
              "city": "Berlin",
              "street": "Street",
              "streetNumber": 34,
              "postalCode": "93-540",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel": [
                  {
                      "name": "Client"
                  }
              ],
              "login": "failedAttempts",
              "email": "failedAttempts@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String accountToGetInformationsJson = """
          {
              "firstName": "Thomas",
              "lastName": "Blocked",
              "country": "Germany",
              "city": "Berlin",
              "street": "Street",
              "streetNumber": 34,
              "postalCode": "93-540",
              "password": "Student123!",
              "locale": "pl",
              "accessLevel": [
                  {
                      "name": "Client"
                  }
              ],
              "login": "getInformations",
              "email": "betInformations@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String notVerifiedAccountToActivateJson = """
          {
              "firstName": "Thomas",
              "lastName": "NotVerified",
              "country": "Germany",
              "city": "Berlin",
              "street": "Street",
              "streetNumber": 34,
              "postalCode": "93-540",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel": [
                  {
                      "name": "Client"
                  }
              ],
              "login": "notVerifiedThomas",
              "email": "notVerifiedToActivate@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String activeAccountToActivateJson = """
          {
              "firstName": "Thomas",
              "lastName": "Active",
              "country": "Germany",
              "city": "Berlin",
              "street": "Street",
              "streetNumber": 34,
              "postalCode": "93-540",
              "password": "Password123!",
              "locale": "pl",
              "accessLevel": [
                  {
                      "name": "Client"
                  }
              ],
              "login": "activeThomas",
              "email": "activeToActivate@example.com",
              "timeZone": "EUROPE_WARSAW"
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
              "email": "toeditaccesslevel@example.com",
              "timeZone": "EUROPE_WARSAW"
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
              "email": "toedit@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String accountToAddAccessLevelsJson = """
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
              "login": "addaccesslevel",
              "email": "addaccesslevel@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String accountToRemoveAccessLevelsJson = """
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
              "login": "removeaccesslevel",
              "email": "removeaccesslevel@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String accountToChangeAccessLevelsJson = """
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
              "login": "changeaccesslevel",
              "email": "changeaccesslevel@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String accountToChangeModeJson = """
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
              "login": "changeMode",
              "email": "changeMode@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String accountToBlock1Json = """
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
              "login": "blockaccount1",
              "email": "blockaccount1@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String accountToBlock2Json = """
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
              "login": "blockaccount2",
              "email": "blockaccount2@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String accountToBlock3Json = """
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
              "login": "blockaccount3",
              "email": "blockaccount3@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;
  public static String accountToBlock4Json = """
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
              "login": "blockaccount4",
              "email": "blockaccount4@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;
  public static String accountToBlock5Json = """
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
              "login": "blockaccount5",
              "email": "blockaccount5@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;
  public static String accountToBlock6Json = """
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
              "login": "blockaccount6",
              "email": "blockaccount6@example.com",
              "timeZone": "EUROPE_WARSAW"
          }
      """;

  public static String accountToLogin = """
          {
              "firstName": "John",
              "lastName": "Boe",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Student123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Employee"
                },
              "login": "loginlogin",
              "email": "loginlogin@example.com",
              "timeZone": "EUROPE_WARSAW"
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

  public static String changePasswordJsonTemplate = """
      {
        "currentPassword": "%s",
        "password": "%s"
      }
      """;
}
