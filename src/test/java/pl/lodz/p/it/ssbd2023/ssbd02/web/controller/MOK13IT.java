package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT_LANGUAGE;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
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
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("MOK.13 - Login")
public class MOK13IT {
  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Init {
    @Test
    @DisplayName("Should properly create account1")
    @Order(1)
    void shouldProperlyCreateAccount1() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .contentType("application/json")
              .body(InitData.accountToLogin)
              .when()
              .post("/account/create")
              .then()
              .statusCode(201);
    }
  }

  @Nested
  @Order(2)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Positive {

    @DisplayName("Should properly login")
    @ParameterizedTest(name = "login: {0}")
    @CsvSource({
            "loginlogin"
    })
    @Order(1)
    void shouldProperlyLogin(String login) {
      UserCredentialsDto userCredentialsDto = UserCredentialsDto.builder()
                      .login(login)
                      .password("Student123!")
                      .build();
      given()
              .header(ACCEPT_LANGUAGE, "en-US")
              .contentType("application/json")
              .body(InitData.mapToJsonString(userCredentialsDto))
              .when()
              .post("/account/login")
              .then()
              .statusCode(200)
              .contentType("application/json")
              .body("token", notNullValue())
              .body("token", not(emptyString()))
              .body("refresh_token", notNullValue())
              .body("refresh_token", not(emptyString()));
    }
  }

  @Nested
  @Order(3)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Negative {

    @DisplayName("Should fail to login with wrong password")
    @ParameterizedTest(name = "login: {0}")
    @CsvSource({
            "loginlogin"
    })
    @Order(1)
    void shouldFailToLoginWithWrongPassword(String login) {
      UserCredentialsDto userCredentialsDto = UserCredentialsDto.builder()
              .login(login)
              .password("invPassword1!")
              .build();
      given()
              .header(ACCEPT_LANGUAGE, "en-US")
              .contentType("application/json")
              .body(InitData.mapToJsonString(userCredentialsDto))
              .when()
              .post("/account/login")
              .then()
              .statusCode(401)
              .contentType("application/json")
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CREDENTIALS));
    }

    @DisplayName("Should fail to login with wrong login")
    @Test
    @Order(3)
    void shouldFailToLoginWithWrongLogin() {
      UserCredentialsDto userCredentialsDto = UserCredentialsDto.builder()
              .login("invalid")
              .password("Student123!")
              .build();
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(userCredentialsDto))
              .when()
              .post("/account/login")
              .then()
              .statusCode(401)
              .contentType("application/json")
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CREDENTIALS));
    }

    @DisplayName("Should fail to login with missing password")
    @ParameterizedTest(name = "login: {0}")
    @CsvSource({
            "loginlogin"
    })
    @Order(4)
    void shouldFailToLoginWithMissingPassword(String login) {
      UserCredentialsDto userCredentialsDto = UserCredentialsDto.builder()
              .login(login)
              .build();
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(userCredentialsDto))
              .when()
              .post("/account/login")
              .then()
              .statusCode(400)
              .contentType("application/json")
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("password"))
              .body("errors[0].message", equalTo("must not be blank"));
    }

    @DisplayName("Should fail to login with missing login")
    @Test
    @Order(5)
    void shouldFailToLoginWithMissingLogin() {
      UserCredentialsDto userCredentialsDto = UserCredentialsDto.builder()
              .password("Student123!")
              .build();
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(userCredentialsDto))
              .when()
              .post("/account/login")
              .then()
              .statusCode(400)
              .contentType("application/json")
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("login"))
              .body("errors[0].message", equalTo("must not be blank"));
    }

    @DisplayName("Should fail to login with empty json in body")
    @Test
    @Order(6)
    void shouldFailToLoginWithEmptyJsonBody() {
      UserCredentialsDto userCredentialsDto = UserCredentialsDto.builder().build();
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(userCredentialsDto))
              .when()
              .post("/account/login")
              .then()
              .statusCode(400)
              .contentType("application/json")
              .body("errors", hasSize(2))
              .body("errors.field", hasItems("password", "login"))
              .body("errors.message", hasItems("must not be blank", "must not be blank"));
    }

    @DisplayName("Should fail to login with empty body")
    @Test
    @Order(6)
    void shouldFailToLoginWithEmptyBody() {
      given()
              .contentType("application/json")
              .body("")
              .when()
              .post("/account/login")
              .then()
              .statusCode(400)
              .contentType("text/html");
    }

    @DisplayName("Should fail to login with missing body")
    @Test
    @Order(7)
    void shouldFailToLoginWithMissingBody() {
      given()
              .contentType("application/json")
              .when()
              .post("/account/login")
              .then()
              .statusCode(400)
              .contentType("text/html");
    }
  }
}
