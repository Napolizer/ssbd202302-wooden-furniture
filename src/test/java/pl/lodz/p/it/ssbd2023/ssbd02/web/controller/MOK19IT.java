package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT_LANGUAGE;
import static org.hamcrest.Matchers.equalTo;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.19 - Block account after 3 failed attempts")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK19IT {
  private String login = "failedAttempts";
  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {

    @Test
    @DisplayName("Should properly create account with Employee access level to edit access levels")
    @Order(1)
    void shouldProperlyCreateAccountWithEmployeeAccessLevelToEditAccessLevels() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .contentType("application/json")
              .body(InitData.blockAccountAfterFailedAttempts)
              .when()
              .post("/account/create")
              .then()
              .statusCode(201);
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {
    @Test
    @DisplayName("Should fail to login with wrong password for the first time")
    @Order(1)
    void shouldFailToLoginWithWrongPasswordForFirstTime() {
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

    @DisplayName("Should fail to login with wrong password for the second time")
    @Order(2)
    void shouldFailToLoginWithWrongPasswordForSecondTime() {
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

    @DisplayName("Should fail to login with wrong password for the third time")
    @Order(3)
    void shouldFailToLoginWithWrongPasswordForThirdTime() {
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

    @DisplayName("Should fail to login with correct password cause of account blockade")
    @Order(4)
    void shouldFailToLoginWithCorrectPasswordCauseOfBlockade() {
      UserCredentialsDto userCredentialsDto = UserCredentialsDto.builder()
              .login(login)
              .password("Password123!")
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
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_BLOCKED));
    }
  }
}
