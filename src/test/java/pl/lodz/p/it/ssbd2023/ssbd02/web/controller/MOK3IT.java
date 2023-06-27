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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("MOK.3 - Block account")
public class MOK3IT {

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Init {
    @Test
    @DisplayName("Should properly create account1")
    @Order(1)
    void shouldProperlyCreateAccount1() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .contentType("application/json")
              .body(InitData.accountToBlock1Json)
              .when()
              .post("/account/create")
              .then()
              .statusCode(201);
    }

    @Test
    @DisplayName("Should properly create account2")
    @Order(2)
    void shouldProperlyCreateAccount2() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .contentType("application/json")
              .body(InitData.accountToBlock2Json)
              .when()
              .post("/account/create")
              .then()
              .statusCode(201);
    }

    @Test
    @DisplayName("Should properly create account3")
    @Order(3)
    void shouldProperlyCreateAccount3() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .contentType("application/json")
              .body(InitData.accountToBlock3Json)
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
    @DisplayName("Should properly block active account")
    @ParameterizedTest(name = "login {0}")
    @CsvSource({
        "blockaccount1",
        "blockaccount2",
        "blockaccount3"
    })
    @Order(1)
    void shouldProperlyBlockActiveAccount(String login) {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
          .when()
          .get("/account/id/" + AccountUtil.getAccountId(login))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("accountState", is(equalTo("ACTIVE")));

      given()
          .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
          .patch("/account/block/" + AccountUtil.getAccountId(login))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("message", is(equalTo("mok.account.block.successful")));

      given()
          .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
          .when()
          .get("/account/id/" + AccountUtil.getAccountId(login))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("accountState", is(equalTo("BLOCKED")));
    }
  }

  @Nested
  @Order(3)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {
    @DisplayName("Should fail to block already blocked account")
    @Test
    @Order(1)
    void shouldFailToBlockAlreadyBlockedAccount() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
          .patch("/account/block/" + AccountUtil.getAccountId("blockaccount1"))
          .then()
          .statusCode(400)
          .contentType("application/json")
          .body("message", is(equalTo("exception.mok.account.change.state")));
    }

    @DisplayName("Should fail to block non existing account")
    @Test
    @Order(2)
    void shouldFailToBlockNotExistingAccount() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
          .patch("/account/block/" + Long.MAX_VALUE)
          .then()
          .statusCode(404)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }

    @DisplayName("Should fail because of missing authorization header")
    @Test
    @Order(3)
    void shouldFailToBlockAccountWithoutAuthorizationHeader() {
      given()
          .patch("/account/block/" + AccountUtil.getAccountId("blockaccount1"))
          .then()
          .statusCode(401);
    }

    @DisplayName("Should fail because of wrong authorization header")
    @Test
    @Order(4)
    void shouldFailToBlockAccountWithWrongAuthorizationHeader() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveSalesRepToken())
          .patch("/account/block/" + AccountUtil.getAccountId("blockaccount1"))
          .then()
          .statusCode(403);
    }
  }
}
