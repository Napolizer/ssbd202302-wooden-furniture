package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT_LANGUAGE;
import static org.hamcrest.Matchers.equalTo;

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
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.19 - Block account after 3 failed attempts")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK19IT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }
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
    @Test
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
    @Test
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
    @Test
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
