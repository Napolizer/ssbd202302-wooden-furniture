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
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
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
@DisplayName("MOK.29 - Unique password")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK29IT {
  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {
    @DisplayName("should properly change password")
    @ParameterizedTest(name = "login: {0}")
    @CsvSource({
        "changepassword",
        "changepas"
    })
    @Order(1)
    void shouldProperlyChangePassword(String login) {
      AccountUtil.createUser(login);
      given()
          .contentType("application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student123!"))
          .body(InitData.changePasswordJsonTemplate.formatted("Student123!", "Student321!"))
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(200);
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {
    @DisplayName("should fail to change password to previous one")
    @ParameterizedTest(name = "login: {0}")
    @CsvSource({
        "arystoteles",
        "sokrates"
    })
    @Order(1)
    void shouldFailToChangePasswordToPreviousOne(String login) {
      AccountUtil.createUser(login);
      given()
          .contentType("application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student123!"))
          .body(InitData.changePasswordJsonTemplate.formatted("Student123!", "Student321!"))
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(200);
      given()
          .contentType("application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student321!"))
          .body(InitData.changePasswordJsonTemplate.formatted("Student321!", "Student123!"))
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(409)
          .body("message", is(equalTo("exception.password.already.used")));
    }

    @DisplayName("should fail to change password to already used one")
    @ParameterizedTest(name = "login: {0}")
    @CsvSource({
        "platon",
        "placyt"
    })
    @Order(2)
    void shouldFailToChangePasswordToAlreadyUsedOne(String login) {
      AccountUtil.createUser(login);
      given()
          .contentType("application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student123!"))
          .body(InitData.changePasswordJsonTemplate.formatted("Student123!", "Student321!"))
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(200);
      given()
          .contentType("application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student321!"))
          .body(InitData.changePasswordJsonTemplate.formatted("Student321!", "Student555!"))
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(200);
      given()
          .contentType("application/json")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student555!"))
          .body(InitData.changePasswordJsonTemplate.formatted("Student555!", "Student123!"))
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(409)
          .body("message", is(equalTo("exception.password.already.used")));
    }
  }
}
