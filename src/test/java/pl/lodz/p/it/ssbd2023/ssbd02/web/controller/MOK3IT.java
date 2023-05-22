package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

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
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("MOK.3 - Block account")
public class MOK3IT {

  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Positive {
    @DisplayName("Should properly block active account")
    @ParameterizedTest(name = "login {0}")
    @CsvSource({
        "client",
        "employee",
        "salesrep"
    })
    @Order(1)
    void shouldProperlyBlockActiveAccount(String login) {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .patch("/account/block/" + AccountUtil.getAccountId(login))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("message", is(equalTo("mok.account.block.successful")));
    }
  }

  @Nested
  @Order(2)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Negative {
    @DisplayName("Should fail to block already blocked account")
    @Test
    @Order(1)
    void shouldFailToBlockAlreadyBlockedAccount() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .patch("/account/block/" + AccountUtil.getAccountId("clientemployee"))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("message", is(equalTo("mok.account.block.successful")));
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .patch("/account/block/" + AccountUtil.getAccountId("clientemployee"))
          .then()
          .statusCode(400)
          .contentType("application/json")
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CHANGE_STATE));
    }

    @DisplayName("Should fail to block non existing account")
    @Test
    @Order(2)
    void shouldFailToBlockNotExistingAccount() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .patch("/account/block/" + Long.MAX_VALUE)
          .then()
          .statusCode(404)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }
  }
}
