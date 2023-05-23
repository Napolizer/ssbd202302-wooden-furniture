package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("MOK.7 - Change own password")
public class MOK7IT {
  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Init {
    @Test
    @DisplayName("Should properly create account")
    @Order(1)
    void shouldProperlyCreateAccount() {
      given()
          .header("Authorization", "Bearer " +
              AuthUtil.retrieveToken("administrator", "Student123!"))
          .contentType("application/json")
          .body(InitData.accountToChangePasswordJson)
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
    @Test
    @DisplayName("Should properly change password")
    @Order(1)
    void shouldProperlyChangePassword() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken(
              "JohnChangePassword", "Student123!"))
          .contentType("application/json")
          .body(InitData.changePasswordJson)
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("login", equalTo("JohnChangePassword"));
    }
  }

  @Nested
  @Order(3)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Negative {
    @Test
    @DisplayName("Should fail because of missing header")
    @Order(1)
    void shouldFailToChangePasswordWithMissingHeader() {
      given()
          .contentType("application/json")
          .body(InitData.changePasswordJson)
          .when()
          .put("/account/self/changePassword")
          .then()
          .statusCode(401);
    }
  }
}
