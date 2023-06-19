package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
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
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("MOK.6 - Delete access level")
public class MOK6IT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }
  private String login = "removeaccesslevel";
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
              .body(InitData.accountToRemoveAccessLevelsJson)
              .when()
              .post("/account/create")
              .then()
              .statusCode(201);
    }

    @Test
    @DisplayName("Account after creation should have assigned employee access level")
    @Order(2)
    void shouldProperlyHaveEmployeeAccessLevel() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .get("/account/id/" + InitData.retrieveAccountId(login))
              .then()
              .statusCode(200)
              .contentType("application/json")
              .body("roles[0]", equalTo("employee"));
    }

    @Test
    @DisplayName("Should properly add client access level when account got only employee access level")
    @Order(3)
    void shouldProperlyAddClientAccessLevelWhenAccountGotOnlyEmployeeAccessLevel() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .put("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/Client")
              .then()
              .statusCode(200);
    }

    @Test
    @DisplayName("Should properly remove client access level")
    @Order(4)
    void shouldProperlyRemoveAccessLevelWhenTwoAreAssigned() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .delete("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/Client")
              .then()
              .statusCode(200);
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {
    @Test
    @DisplayName("Should fail to remove access level without authorization token")
    @Order(1)
    void failsToRemoveAccessLevelWithoutAuthorizationToken() {
      given()
              .when()
              .delete("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/Administrator")
              .then()
              .statusCode(401);
    }

    @Test
    @DisplayName("Should fail to remove access level when account does not exist")
    @Order(2)
    void failsToRemoveAccessLevelWhenAccountDoesNotExist() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .put("account/id/0/accessLevel/Administrator")
              .then()
              .statusCode(404)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }

    @Test
    @DisplayName("Should fail to remove access level when access level is invalid")
    @Order(3)
    void failsToRemoveAccessLevelWhenAccessLevelIsInvalid() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .delete("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/NotValidAccessLevel")
              .then()
              .statusCode(400)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL));
    }

    @Test
    @DisplayName("Should fail to remove access level when access level is not assigned")
    @Order(4)
    void failsToRemoveAccessLevelWhenAccessLevelIsNotAssigned() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .delete("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/Administrator")
              .then()
              .statusCode(400)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_REMOVE_ACCESS_LEVEL));
    }

    @Test
    @DisplayName("Should fail to remove access level when there is only one assigned ")
    @Order(5)
    void failsToRemoveAccessLevelWhenThereIsOnlyOneAccessLevelAssigned() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .delete("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/Employee")
              .then()
              .statusCode(400)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_REMOVE_ACCESS_LEVEL));
    }
  }
}
