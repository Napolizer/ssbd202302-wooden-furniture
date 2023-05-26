package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.http.ContentType;
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
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("MOK.26 - Change access level")
public class MOK26IT {
  private String login = "changeaccesslevel";
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
              .body(InitData.accountToChangeAccessLevelsJson)
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
    @DisplayName("Should change access level")
    @Order(3)
    void shouldProperlyChangeAccessLevel() {
      AccessLevelDto accessLevel = new AccessLevelDto("client");

      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .contentType(ContentType.JSON)
              .body(InitData.mapToJsonString(accessLevel))
              .when()
              .put("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/change")
              .then()
              .statusCode(200)
              .contentType("application/json")
              .body("roles[0]", equalTo("client"));
    }

    @Test
    @DisplayName("Should properly add client access level when account got only employee access level")
    @Order(4)
    void shouldProperlyAddClientAccessLevelWhenAccountGotOnlyEmployeeAccessLevel() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .put("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/employee")
              .then()
              .statusCode(200);
    }

  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {
    @Test
    @DisplayName("Should fail to change access level if there is more than one")
    @Order(1)
    void failsToChangeAccessLevelWithoutAuthorizationToken() {
      AccessLevelDto accessLevel = new AccessLevelDto("administrator");

      given()
              .when()
              .contentType(ContentType.JSON)
              .body(InitData.mapToJsonString(accessLevel))
              .put("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/change")
              .then()
              .statusCode(401);
    }

    @Test
    @DisplayName("Should properly remove client access level")
    @Order(2)
    void shouldProperlyRemoveClientAccessLevel() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .when()
              .delete("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/client")
              .then()
              .statusCode(200);
    }

    @Test
    @DisplayName("Should fail to change access level when account does not exist")
    @Order(3)
    void failsToChangeAccessLevelWhenAccountDoesNotExist() {
      AccessLevelDto accessLevel = new AccessLevelDto("administrator");
      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .contentType(ContentType.JSON)
              .body(InitData.mapToJsonString(accessLevel))
              .when()
              .put("account/id/0/accessLevel/change")
              .then()
              .statusCode(404)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }

    @Test
    @DisplayName("Should fail to change access level when access level is invalid")
    @Order(4)
    void failsToChangeAccessLevelWhenAccessLevelIsInvalid() {
      AccessLevelDto accessLevel = new AccessLevelDto("administratorr");

      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .contentType(ContentType.JSON)
              .body(InitData.mapToJsonString(accessLevel))
              .when()
              .put("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/change")
              .then()
              .statusCode(400)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL));
    }

    @Test
    @DisplayName("Should fail to change access level when access level is invalid")
    @Order(5)
    void failsToChangeAccessLevelWhenBodyIsEmpty() {

      given()
              .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
              .contentType(ContentType.JSON)
              .when()
              .put("/account/id/" + InitData.retrieveAccountId(login) + "/accessLevel/change")
              .then()
              .statusCode(400);
    }
  }
}
