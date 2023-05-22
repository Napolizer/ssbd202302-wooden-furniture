package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.5 - Add access level")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK5IT {
  private String login = "addaccesslevel";
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
              .body(InitData.accountToAddAccessLevelsJson)
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
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {

    @Test
    @DisplayName("Should fail to add access level without authorization token")
    @Order(1)
    void failsToAddAccessLevelWithoutAuthorizationToken() {
      given()
              .when()
              .put("/account/id/" + InitData.retrieveAccountId("administrator") + "/accessLevel/SalesRep")
              .then()
              .statusCode(401);
    }

    @Test
    @DisplayName("Should fail to add access level when account does not exist")
    @Order(2)
    void failsToAddAccessLevelWhenAccountDoesNotExist() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .put("account/id/0/accessLevel/Sales_Rep")
              .then()
              .statusCode(404)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }

    @Test
    @DisplayName("Should fail to add access level when access level is invalid")
    @Order(3)
    void failsToAddAccessLevelWhenAccessLevelIsInvalid() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .put("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/NotValidAccessLevel")
              .then()
              .statusCode(400)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL));
    }

    @Test
    @DisplayName("Should fail to add access level when access level is already assigned")
    @Order(4)
    void failsToAddAccessLevelWhenAccessLevelIsAlreadyAssigned() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .put("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/Employee")
              .then()
              .statusCode(400)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL_ALREADY_ASSIGNED));
    }

    @Test
    @DisplayName("Should fail to add administrator access level")
    @Order(5)
    void failsToAddAdministratorAccessLevel() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .put("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/Administrator")
              .then()
              .statusCode(400)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_ADMINISTRATOR_ACCESS_LEVEL_ALREADY_ASSIGNED));
    }

    @Test
    @DisplayName("Should fail to add sales rep access level when employee is assigned")
    @Order(6)
    void failsToAddSalesRepWhenEmployeeIsAlreadyAssignedAccessLevel() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .put("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/Sales_rep")
              .then()
              .statusCode(400)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CLIENT_AND_SALES_REP_ACCESS_LEVEL_CONFLICT));
    }
  }
}
