package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.IF_MATCH;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("MOK.20 - Change email")
class MOK20IT {

  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Positive {
    @DisplayName("Should properly change email")
    @ParameterizedTest(name = "login: {0}, email: {1}")
    @CsvSource({
        "client,client.new.email@ssbd.com",
        "salesrep,salesrep.new.email@ssbd.com",
        "employee,employee.new.email@ssbd.com",
        "clientemployee,clientemployee.new.email@ssbd.com"
    })
    @Order(1)
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
        "client,administrator",
        "client,salesrep",
        "salesrep,client",
        "clientemployee,client",
        "employee,clientemployee",
    })
    @Order(1)
    void shouldFailToChangeOtherUserEmail(String login, String otherLogin) {
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken(login, "Student123!"))
          .body("""
                     {
                         "email": "user@ssbd.com"
                     }
              """)
          .when()
          .put("/account/change-email/" + AccountUtil.getAccountId(otherLogin))
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
          .put("/account/change-email/" + AccountUtil.getAccountId("client"))
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
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client", "Student123!"))
          .body("""
                     {
                         "email": "         "
                     }
              """)
          .when()
          .put("/account/change-email/" + AccountUtil.getAccountId("client"))
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
        "client,client.new.email@ssbd.com,1",
        "administrator,admin.new.email@ssbd.com,1",
        "salesrep,salesrep.new.email@ssbd.com,1",
        "employee,employee.new.email@ssbd.com,1",
        "clientemployee,clientemployee.new.email@ssbd.com,1"
    })
    @Order(6)
    void shouldFailToChangeEmailIfVersionIsInvalid(String login, String email, String version) {
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken(login, "Student123!"))
          .header(IF_MATCH, version)
          .body("""
                     {
                         "email": "%s"
                     }
              """.formatted(email))
          .when()
          .put("/account/change-email/" + AccountUtil.getAccountId(login))
          .then()
          .statusCode(409)
          .contentType("application/json")
          .body("message", is(equalTo("exception.optimistic.lock")));
    }

  }
}
