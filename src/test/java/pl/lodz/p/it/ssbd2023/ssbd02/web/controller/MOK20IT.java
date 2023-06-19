package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.HttpHeaders.IF_MATCH;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("MOK.20 - Change email")
class MOK20IT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }

  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Positive {


    @Order(1)
    @DisplayName("Should properly create account to edit")
    @ParameterizedTest(name = "login: {0}")
    @CsvSource({
            "bednaro",
            "bednaroo",
            "bednarooo",
            "bednaroooo",
            "bednarooooo"
    })
    void shouldProperlyCreateAccountsToEmails(String login) {
      int id = AccountUtil.registerUser(login);
      given()
              .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
              .when()
              .patch("/account/activate/" + id)
              .then()
              .statusCode(200);
      switch (login) {
        case "bednaroo" -> {
          AccessLevelDto accessLevelDto = new AccessLevelDto(ADMINISTRATOR);
          given()
                  .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
                  .contentType("application/json")
                  .when()
                  .body(InitData.mapToJsonString(accessLevelDto))
                  .put("/account/id/" + id + "/accessLevel/change")
                  .then()
                  .statusCode(200);
        }
        case "bednarooo" -> {
          AccessLevelDto accessLevelDto = new AccessLevelDto(EMPLOYEE);
          given()
                  .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
                  .contentType("application/json")
                  .when()
                  .body(InitData.mapToJsonString(accessLevelDto))
                  .put("/account/id/" + id + "/accessLevel/change")
                  .then()
                  .statusCode(200);
        }
        case "bednaroooo" -> {
          AccessLevelDto accessLevelDto = new AccessLevelDto(SALES_REP);
          given()
                  .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
                  .contentType("application/json")
                  .when()
                  .body(InitData.mapToJsonString(accessLevelDto))
                  .put("/account/id/" + id + "/accessLevel/change")
                  .then()
                  .statusCode(200);
        }
        case "bednarooooo" -> {
          AccessLevelDto accessLevelDto = new AccessLevelDto(EMPLOYEE);
          given()
                  .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
                  .contentType("application/json")
                  .when()
                  .body(InitData.mapToJsonString(accessLevelDto))
                  .put("/account/id/" + id + "/accessLevel/employee")
                  .then()
                  .statusCode(200);
        }
      }
    }

    @DisplayName("Should properly change email")
    @ParameterizedTest(name = "login: {0}, email: {1}")
    @CsvSource({
        "bednaro,bednaro.new.email@ssbd.com",
        "bednaroo,bednaroo.new.email@ssbd.com",
        "bednarooo,bednarooo.new.email@ssbd.com",
        "bednaroooo,bednaroooo.new.email@ssbd.com"
    })
    @Order(2)
    void shouldProperlyChangeEmail(String login, String email) {
      String token = AuthUtil.retrieveToken(login, "Student123!");
      String version = InitData.retrieveVersion(login);
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer " + token)
          .header(IF_MATCH, version)
          .body("""
                     {
                         "email": "%s"
                     }
              """.formatted(email))
          .when()
          .put("/account/change-email/" + AccountUtil.getAccountId(login))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("message", is(equalTo("mok.email.change.successful")));
    }
  }

  @Nested
  @Order(2)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Negative {
    @DisplayName("Should fail to change other person email")
    @ParameterizedTest(name = "login: {0}, otherLogin: {1}")
    @CsvSource({
        "bednaro,bednaroo",
        "bednaro,bednaroooo",
        "bednaroooo,bednaro",
        "bednarooooo,bednaro",
        "bednarooo,bednarooooo",
    })
    @Order(1)
    void shouldFailToChangeOtherUserEmail(String login, String otherLogin) {
      int id = AccountUtil.getAccountId(otherLogin);
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken(login, "Student123!"))
          .body("""
                     {
                         "email": "user@ssbd.com"
                     }
              """)
          .when()
          .put("/account/change-email/" + id)
          .then()
          .statusCode(403)
          .contentType("application/json")
          .body("message", is(equalTo("exception.forbidden")));
    }

    @DisplayName("Should fail to change email in invalid format")
    @ParameterizedTest(name = "invalid email: {0}")
    @CsvSource({
        "blabla",
        "123",
        "@",
        "null",
        "abc@",
        "@123"
    })
    @Order(2)
    void shouldFailToChangeEmailInInvalidFormat(String invalidEmail) {
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client", "Student123!"))
          .body("""
                     {
                         "email": "%s"
                     }
              """.formatted(invalidEmail))
          .when()
          .put("/account/change-email/" + AccountUtil.getAccountId("bednaro"))
          .then()
          .statusCode(400)
          .contentType("application/json")
          .body("errors", notNullValue())
          .body("errors.size()", is(equalTo(1)))
          .body("errors[0].message", is(equalTo("must be a well-formed email address")));
    }

    @DisplayName("Should fail to change email if email is empty")
    @Test
    @Order(3)
    void shouldFailToChangeEmailIfEmailIsEmpty() {
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client", "Student123!"))
          .body("""
                     {
                         "email": ""
                     }
              """)
          .when()
          .put("/account/change-email/" + AccountUtil.getAccountId("client"))
          .then()
          .statusCode(400)
          .contentType("application/json")
          .body("errors", notNullValue())
          .body("errors.size()", is(equalTo(1)))
          .body("errors[0].message", is(equalTo("must not be blank")));
    }

    @DisplayName("Should fail to change email if email is blank")
    @Test
    @Order(4)
    void shouldFailToChangeEmailIfEmailIsBlank() {
      int id = AccountUtil.getAccountId("bednaro");
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client", "Student123!"))
          .body("""
                     {
                         "email": "         "
                     }
              """)
          .when()
          .put("/account/change-email/" + id)
          .then()
          .statusCode(400)
          .contentType("application/json")
          .body("errors", notNullValue())
          .body("errors.size()", is(equalTo(2)));
    }

    @DisplayName("Should fail to change email if account does not exist")
    @ParameterizedTest(name = "non-existing account: {0}")
    @CsvSource({
        "3232323",
        "12345",
        "4444",
        "444"
    })
    @Order(5)
    void shouldFailToChangeEmailIfAccountDoesNotExist(String nonExistingAccount) {
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client", "Student123!"))
          .body("""
                     {
                         "email": "user@ssbd.com"
                     }
              """)
          .when()
          .put("/account/change-email/" + nonExistingAccount)
          .then()
          .statusCode(403);
    }

    @DisplayName("Should fail to change email if version is invalid")
    @ParameterizedTest(name = "login: {0}, email: {1}, version: {2}")
    @CsvSource({
        "bednaro,bednaro.new.email@ssbd.com,1",
        "bednaroo,bednaroo.new.email@ssbd.com,1",
        "bednarooo,bednarooo.new.email@ssbd.com,1",
        "bednaroooo,bednaroooo.new.email@ssbd.com,1",
        "bednarooooo,bednarooooo.new.email@ssbd.com,1"
    })
    @Order(6)
    void shouldFailToChangeEmailIfVersionIsInvalid(String login, String email, String version) {
      String token = AuthUtil.retrieveToken(login, "Student123!");
      int id = AccountUtil.getAccountId(login);
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer " + token)
          .header(IF_MATCH, version)
          .body("""
                     {
                         "email": "%s"
                     }
              """.formatted(email))
          .when()
          .put("/account/change-email/" + id)
          .then()
          .statusCode(409)
          .contentType("application/json")
          .body("message", is(equalTo("exception.optimistic.lock")));
    }

  }
}
