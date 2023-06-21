package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("MOK.7 - Change password")
public class MOK7IT {
  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {
    @DisplayName("Should properly change password")
    @ParameterizedTest(name = "login: {0}")
    @CsvSource({
        "asterix",
        "obelix"
    })
    @Order(1)
    void shouldProperlyChangePassword(String login) {
      AccountUtil.createUser(login);
      given()
          .contentType("application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student123!"))
          .body("""
                     {
                         "currentPassword": "Student123!",
                         "password": "Student321!"
                     }
              """)
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("login", is(equalTo(login)));
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {
    @DisplayName("Should fail to change password to previous one")
    @ParameterizedTest(name = "login: {0}")
    @CsvSource({
        "panoramix",
        "kakofonix"
    })
    @Order(1)
    void shouldFailToChangePasswordToPreviousOne(String login) {
      AccountUtil.createUser(login);
      given()
          .contentType("application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student123!"))
          .body("""
                     {
                         "currentPassword": "Student123!",
                         "password": "Student321!"
                     }
              """)
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("login", is(equalTo(login)));
      given()
          .contentType("application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student321!"))
          .body("""
                     {
                         "currentPassword": "Student321!",
                         "password": "Student123!"
                     }
              """)
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(409)
          .contentType("application/json")
          .body("message", is(equalTo("exception.password.already.used")));
    }

    @DisplayName("Should fail to change password to already used one")
    @ParameterizedTest(name = "login: {0}")
    @CsvSource({
        "idefix",
        "miraculix"
    })
    @Order(2)
    void shouldFailToChangePasswordToAlreadyUsedOne(String login) {
          AccountUtil.createUser(login);
          given()
              .contentType("application/json")
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student123!"))
              .body("""
                     {
                         "currentPassword": "Student123!",
                         "password": "Student321!"
                     }
              """)
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("login", is(equalTo(login)));
      given()
          .contentType("application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student321!"))
          .body("""
                     {
                         "currentPassword": "Student321!",
                         "password": "Student555!"
                     }
              """)
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("login", is(equalTo(login)));
      given()
          .contentType("application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student555!"))
          .body("""
                     {
                         "currentPassword": "Student555!",
                         "password": "Student123!"
                     }
              """)
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(409)
          .contentType("application/json")
          .body("message", is(equalTo("exception.password.already.used")));
    }

    @Test
    @DisplayName("Should fail to change password without authorization header")
    @Order(3)
    void shouldFailToChangePasswordWithoutAuthorizationHeader() {
      AccountUtil.createUser("testofix");
      given()
          .contentType("application/json")
          .body("""
                     {
                         "currentPassword": "Student123!",
                         "password": "Student321!"
                     }
              """)
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(401);
    }

    @Test
    @DisplayName("Should fail to change password with invalid currentPassword")
    @Order(4)
    void shouldFailToChangePasswordWithInvalidCurrentPassword() {
      AccountUtil.createUser("testofonix");
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("testofonix", "Student123!"))
          .contentType("application/json")
          .body("""
                     {
                         "currentPassword": "student123!",
                         "password": "Student321!"
                     }
              """)
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("Should fail to change password with invalid password")
    @Order(5)
    void shouldFailToChangePasswordWithInvalidPassword() {
      AccountUtil.createUser("testofarafix");
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("testofarafix", "Student123!"))
          .contentType("application/json")
          .body("""
                     {
                         "currentPassword": "Student123!",
                         "password": "student321!"
                     }
              """)
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(400);
    }
  }
}
