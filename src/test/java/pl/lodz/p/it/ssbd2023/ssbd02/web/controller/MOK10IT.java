package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.10 - Edit account as admin")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK10IT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }
  private static final String accountToEditAsAdminLogin = "accountToEditAsAdm";

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {

    @Test
    @Order(1)
    @DisplayName("Should properly create account to edit as admin")
    void shouldProperlyCreateAccountToEdit() {
      int id = AccountUtil.registerUser(accountToEditAsAdminLogin);
      given()
              .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
              .when()
              .patch("/account/activate/" + id)
              .then()
              .statusCode(200);
    }

    @Test
    @Order(2)
    @DisplayName("Should properly edit account as admin")
    void shouldProperlyEditAccountAsAdmin() {
      String token = AuthUtil.retrieveToken(ADMINISTRATOR);
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditAsAdminLogin);
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditAsAdminLogin + "/editAccountAsAdmin")
              .then()
              .statusCode(200)
              .contentType("application/json");

      AccountWithoutSensitiveDataDto account = given()
              .header("Authorization", "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
              .contentType("application/json")
              .when()
              .get("/account/login/" + accountToEditAsAdminLogin)
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
    @DisplayName("Should fail to edit account as admin with invalid login")
    void shouldFailToEditAccountAsAdminWithInvalidLogin() {
      String token = AuthUtil.retrieveToken(ADMINISTRATOR);
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditAsAdminLogin);
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .accept("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/xxxxxxx/editAccountAsAdmin")
              .then()
              .statusCode(404)
              .contentType("application/json");
    }

    @Test
    @Order(2)
    @DisplayName("Should fail to edit account as admin with invalid role")
    void shouldFailToEditAccountAsAdminWithInvalidRole() {
      String token = AuthUtil.retrieveToken(CLIENT);
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditAsAdminLogin);
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .accept("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/"  + accountToEditAsAdminLogin + "/editAccountAsAdmin")
              .then()
              .statusCode(403);
    }

    @Test
    @Order(3)
    @DisplayName("Should fail to edit account as admin with empty data")
    void shouldFailToEditAccountAsAdminWithEmptyData() {
      String token = AuthUtil.retrieveToken(ADMINISTRATOR);
      EditPersonInfoDto emptyData = EditPersonInfoDto.builder().build();
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(emptyData))
              .when()
              .put("/account/login/" + accountToEditAsAdminLogin + "/editAccountAsAdmin")
              .then()
              .statusCode(400)
              .body("errors", hasSize(8));
    }

    @Test
    @Order(5)
    @DisplayName("Should fail to edit account as admin with invalid last name format")
    void shouldFailToEditAccountAsAdminWithInvalidLastNameFormat() {
      String token = AuthUtil.retrieveToken(ADMINISTRATOR);
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditAsAdminLogin);
      editedAccount.setLastName("invalidlastname");
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditAsAdminLogin + "/editAccountAsAdmin")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("lastName"))
              .body("errors[0].message", equalTo("Field must start with a capital letter and contain only letters"));
    }

    @Test
    @Order(6)
    @DisplayName("Should fail to edit account as admin with invalid country format")
    void shouldFailToEditAccountAsAdminWithInvalidCountryFormat() {
      String token = AuthUtil.retrieveToken(ADMINISTRATOR);
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditAsAdminLogin);
      editedAccount.setCountry("invalidcountry");
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditAsAdminLogin + "/editAccountAsAdmin")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("country"))
              .body("errors[0].message", equalTo("Field must start with a capital letter and contain only letters"));
    }

    @Test
    @Order(7)
    @DisplayName("Should fail to edit account as admin with invalid city format")
    void shouldFailToEditAccountAsAdminWithInvalidCityFormat() {
      String token = AuthUtil.retrieveToken(ADMINISTRATOR);
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditAsAdminLogin);
      editedAccount.setCity("invalidcity");
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditAsAdminLogin + "/editAccountAsAdmin")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("city"))
              .body("errors[0].message", equalTo("Field must start with a capital letter and contain only letters"));
    }

    @Test
    @Order(8)
    @DisplayName("Should fail to edit account as admin with invalid street format")
    void shouldFailToEditAccountAsAdminWithInvalidStreetFormat() {
      String token = AuthUtil.retrieveToken(ADMINISTRATOR);
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditAsAdminLogin);
      editedAccount.setStreet("invalidstreet");
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditAsAdminLogin + "/editAccountAsAdmin")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("street"))
              .body("errors[0].message", equalTo("Field must start with a capital letter and contain only letters"));
    }

    @Test
    @Order(9)
    @DisplayName("Should fail to edit account as admin with invalid postal code format")
    void shouldFailToEditAccountAsAdminWithInvalidPostalCodeFormat() {
      String token = AuthUtil.retrieveToken(ADMINISTRATOR);
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditAsAdminLogin);
      editedAccount.setPostalCode("invalidpostalcode");
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditAsAdminLogin + "/editAccountAsAdmin")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("postalCode"))
              .body("errors[0].message", equalTo("Postal code must be in the format xx-xxx"));
    }

    @Test
    @Order(10)
    @DisplayName("Should fail to edit account as admin with invalid street number format")
    void shouldFailToEditAccountAsAdminWithInvalidStreetNumberFormat() {
      String token = AuthUtil.retrieveToken(ADMINISTRATOR);
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditAsAdminLogin);
      editedAccount.setStreetNumber(-2);
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditAsAdminLogin + "/editAccountAsAdmin")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("streetNumber"))
              .body("errors[0].message", equalTo("Street number must be a positive integer"));
    }

    @Test
    @Order(11)
    @DisplayName("Should fail to edit account as admin when OptimisticLockException is thrown")
    void shouldFailToEditAccountAsAdminWhenOptimisticLockExceptionIsThrown() {
      String token = AuthUtil.retrieveToken(ADMINISTRATOR);
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditAsAdminLogin);
      editedAccount.setFirstName("Jan");
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditAsAdminLogin + "/editAccountAsAdmin")
              .then()
              .statusCode(200)
              .contentType("application/json");

      given()
              .header("Authorization", "Bearer " + token)
              .contentType("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/" + accountToEditAsAdminLogin + "/editAccountAsAdmin")
              .then()
              .statusCode(409)
              .contentType("application/json");
    }

    @Test
    @Order(12)
    @DisplayName("Should fail to edit account as admin without token")
    void shouldFailToEditAccountAsAdminWithoutToken() {
      EditPersonInfoDto editedAccount = InitData.getEditedAccount(accountToEditAsAdminLogin);
      given()
              .contentType("application/json")
              .accept("application/json")
              .body(InitData.mapToJsonString(editedAccount))
              .when()
              .put("/account/login/"  + accountToEditAsAdminLogin + "/editAccountAsAdmin")
              .then()
              .statusCode(401);
    }
  }
}
