package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.9 - Edit own account")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK9IT {
  private static final String accountToEditLogin = "accounttoedit123";

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {

    @Test
    @Order(1)
    @DisplayName("Should properly create account to edit")
    void shouldProperlyCreateAccountToEdit() {
      int id = AccountUtil.registerUser(accountToEditLogin);
      given()
              .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
              .when()
              .patch("/account/activate/" + id)
              .then()
              .statusCode(200);
    }

    @Test
    @Order(2)
    @DisplayName("Should properly edit own account")
    void shouldProperlyEditOwnAccount() {
      String token = AuthUtil.retrieveToken(accountToEditLogin, "Student123!");
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditLogin);
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditLogin + "/editOwnAccount")
              .then()
              .statusCode(200)
              .contentType("application/json");

      AccountWithoutSensitiveDataDto account = given()
              .header("Authorization", "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
              .contentType("application/json")
              .when()
              .get("/account/login/" + accountToEditLogin)
              .then()
              .statusCode(200)
              .extract()
              .body()
              .as(AccountWithoutSensitiveDataDto.class);

      assertEquals(editedAccount.getFirstName(), account.getFirstName());
      assertEquals(editedAccount.getLastName(), account.getLastName());
      assertEquals(editedAccount.getCountry(), account.getAddress().getCountry());
      assertEquals(editedAccount.getCity(), account.getAddress().getCity());
      assertEquals(editedAccount.getStreet(), account.getAddress().getStreet());
      assertEquals(editedAccount.getStreetNumber(), account.getAddress().getStreetNumber());
      assertEquals(editedAccount.getPostalCode(), account.getAddress().getPostalCode());
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {

    @Test
    @Order(1)
    @DisplayName("Should fail to edit own account with invalid login")
    void shouldFailToEditOwnAccountWithInvalidLogin() {
      String token = AuthUtil.retrieveToken(accountToEditLogin, "Student123!");
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditLogin);
      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .contentType("application/json")
          .accept("application/json")
          .body(InitData.mapToJsonString(editedAccount))
          .when()
          .put("/account/login/randomLogin/editOwnAccount")
          .then()
          .statusCode(404)
          .contentType("application/json");
    }

    @Test
    @Order(2)
    @DisplayName("Should fail to edit other account")
    void shouldFailToEditOtherAccount() {
      String token = AuthUtil.retrieveToken(CLIENT);
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditLogin);
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .accept("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/"  + accountToEditLogin + "/editOwnAccount")
              .then()
              .statusCode(403)
              .contentType("application/json")
              .body("message", equalTo("exception.forbidden"));
    }

    @Test
    @Order(3)
    @DisplayName("Should fail to edit own account with empty data")
    void shouldFailToEditOwnAccountWithEmptyData() {
      String token = AuthUtil.retrieveToken(accountToEditLogin, "Student123!");
      EditPersonInfoDto emptyData = EditPersonInfoDto.builder().build();
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(emptyData))
              .when()
              .put("/account/login/" + accountToEditLogin + "/editOwnAccount")
              .then()
              .statusCode(400)
              .body("errors", hasSize(8));
    }

    @Test
    @Order(5)
    @DisplayName("Should fail to edit own account with invalid last name format")
    void shouldFailToEditOwnAccountWithInvalidLastNameFormat() {
      String token = AuthUtil.retrieveToken(accountToEditLogin, "Student123!");
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditLogin);
      editedAccount.setLastName("invalidlastname");
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditLogin + "/editOwnAccount")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("lastName"))
              .body("errors[0].message", equalTo("Field must start with a capital letter and contain only letters"));
    }

    @Test
    @Order(6)
    @DisplayName("Should fail to edit own account with invalid country format")
    void shouldFailToEditOwnAccountWithInvalidCountryFormat() {
      String token = AuthUtil.retrieveToken(accountToEditLogin, "Student123!");
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditLogin);
      editedAccount.setCountry("invalidcountry");
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditLogin + "/editOwnAccount")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("country"))
              .body("errors[0].message", equalTo("Field must start with a capital letter and contain only letters"));
    }

    @Test
    @Order(7)
    @DisplayName("Should fail to edit own account with invalid city format")
    void shouldFailToEditOwnAccountWithInvalidCityFormat() {
      String token = AuthUtil.retrieveToken(accountToEditLogin, "Student123!");
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditLogin);
      editedAccount.setCity("invalidcity");
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditLogin + "/editOwnAccount")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("city"))
              .body("errors[0].message", equalTo("Field must start with a capital letter and contain only letters"));
    }

    @Test
    @Order(8)
    @DisplayName("Should fail to edit own account with invalid street format")
    void shouldFailToEditOwnAccountWithInvalidStreetFormat() {
      String token = AuthUtil.retrieveToken(accountToEditLogin, "Student123!");
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditLogin);
      editedAccount.setStreet("invalidstreet");
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditLogin + "/editOwnAccount")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("street"))
              .body("errors[0].message", equalTo("Field must start with a capital letter and contain only letters"));
    }

    @Test
    @Order(9)
    @DisplayName("Should fail to edit own account with invalid postal code format")
    void shouldFailToEditOwnAccountWithInvalidPostalCodeFormat() {
      String token = AuthUtil.retrieveToken(accountToEditLogin, "Student123!");
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditLogin);
      editedAccount.setPostalCode("invalidpostalcode");
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditLogin + "/editOwnAccount")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("postalCode"))
              .body("errors[0].message", equalTo("Postal code must be in the format xx-xxx"));
    }

    @Test
    @Order(10)
    @DisplayName("Should fail to edit own account with invalid street number format")
    void shouldFailToEditOwnAccountWithInvalidStreetNumberFormat() {
      String token = AuthUtil.retrieveToken(accountToEditLogin, "Student123!");
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditLogin);
      editedAccount.setStreetNumber(-2);
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditLogin + "/editOwnAccount")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("streetNumber"))
              .body("errors[0].message", equalTo("Street number must be a positive integer"));
    }

    @Test
    @Order(11)
    @DisplayName("Should fail to edit own account when OptimisticLockException is thrown")
    void shouldFailToEditOwnAccountWhenOptimisticLockExceptionIsThrown() {
      String token = AuthUtil.retrieveToken(accountToEditLogin, "Student123!");
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditLogin);
      editedAccount.setFirstName("Jan");
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditLogin + "/editOwnAccount")
              .then()
              .statusCode(200)
              .contentType("application/json");

      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditLogin + "/editOwnAccount")
              .then()
              .statusCode(409)
              .contentType("application/json");
    }

    @Test
    @Order(12)
    @DisplayName("Should fail to edit own account without token")
    void shouldFailToEditOwnAccountWithoutToken() {
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditLogin);
      given()
              .contentType("application/json")
              .accept("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/"  + accountToEditLogin + "/editOwnAccount")
              .then()
              .statusCode(401);
    }
  }
}
