package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.4 - Activate account")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK4IT {
  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Init {
    @DisplayName("Should properly create account for first positive test")
    @Test
    @Order(1)
    void shouldProperlyCreateFirstAccountToActivate() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .contentType("application/json")
          .body(InitData.blockedAccountToActivateJson)
          .when()
          .post("/account/create")
          .then()
          .statusCode(201);

      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .patch("/account/block/" + AccountUtil.getAccountId("blockedThomas"))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("message", is(equalTo("mok.account.block.successful")));
    }

    @DisplayName("Should properly create account for second positive test")
    @Test
    @Order(2)
    void shouldProperlyCreateSecondAccountToActivate() {
      given()
          .contentType("application/json")
          .body(InitData.notVerifiedAccountToActivateJson)
          .when()
          .post("/account/register")
          .then()
          .statusCode(201);
    }

    @DisplayName("Should properly create account for negative test")
    @Test
    @Order(3)
    void shouldProperlyCreateThirdAccountToActivate() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .contentType("application/json")
          .body(InitData.activeAccountToActivateJson)
          .when()
          .post("/account/create")
          .then()
          .statusCode(201);
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {
    @DisplayName("Should properly activate blocked account")
    @Test
    @Order(1)
    void shouldProperlyActivateBlockedAccount() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
          .when()
          .get("/account/id/" + AccountUtil.getAccountId("blockedThomas"))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("accountState", is(equalTo("BLOCKED")));

      given()
          .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
          .patch("/account/activate/" + AccountUtil.getAccountId("blockedThomas"))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("message", is(equalTo("mok.account.activate.successful")));

      given()
          .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
          .when()
          .get("/account/id/" + AccountUtil.getAccountId("blockedThomas"))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("accountState", is(equalTo("ACTIVE")));
    }

    @DisplayName("Should properly activate not verified account")
    @Test
    @Order(2)
    void shouldProperlyActivateNotVerifiedAccount() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
          .when()
          .get("/account/id/" + AccountUtil.getAccountId("notVerifiedThomas"))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("accountState", is(equalTo("NOT_VERIFIED")));

      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .patch("/account/activate/" + AccountUtil.getAccountId("notVerifiedThomas"))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("message", is(equalTo("mok.account.activate.successful")));

      given()
          .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
          .when()
          .get("/account/id/" + AccountUtil.getAccountId("notVerifiedThomas"))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("accountState", is(equalTo("ACTIVE")));
    }
  }

  @Nested
  @Order(3)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {
    @DisplayName("Should fail to activate already active account")
    @Test
    @Order(1)
    void shouldFailToActivateAlreadyActiveAccount() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .patch("/account/activate/" + AccountUtil.getAccountId("activeThomas"))
          .then()
          .statusCode(400)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CHANGE_STATE));
    }

    @DisplayName("Should fail to activate not existing account")
    @Test
    @Order(2)
    void shouldFailToActivateNotExistingAccount() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .patch("/account/activate/" + Long.MAX_VALUE)
          .then()
          .statusCode(404)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }

    @DisplayName("Should fail because of missing authorization header")
    @Test
    @Order(3)
    void shouldFailToActivateAccountWithoutAuthorizationHeader() {
      given()
          .patch("/account/activate/" + AccountUtil.getAccountId("blockedThomas"))
          .then()
          .statusCode(401);
    }

    @DisplayName("Should fail because of wrong authorization header")
    @Test
    @Order(4)
    void shouldFailToActivateAccountWithWrongAuthorizationHeader() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveSalesRepToken())
          .patch("/account/activate/" + AccountUtil.getAccountId("blockedThomas"))
          .then()
          .statusCode(403);
    }
  }
}





