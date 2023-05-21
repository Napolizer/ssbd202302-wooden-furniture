package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.http.Header;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class AccountControllerIT {

  private String retrieveAdminToken() {
    return retrieveToken("administrator", "Student123!");
  }

  private String retrieveClientToken() {
    return retrieveToken("client", "Student123!");
  }

  private String retrieveToken(String login, String password) {
    return given()
        .contentType("application/json")
        .header(acceptLanguageHeader)
        .body("""
                   {
                       "login": "$login",
                       "password": "$password"
                   }
            """.replace("$login", login).replace("$password", password))
        .when()
        .post("/account/login")
        .then()
        .statusCode(200)
        .contentType("application/json")
        .extract()
        .path("token");
  }

  private int retrieveAccountId(String login) {
    return given()
        .header("Authorization", "Bearer " + retrieveAdminToken())
        .header(acceptLanguageHeader)
        .when()
        .get("/account/login/" + login)
        .then()
        .statusCode(200)
        .extract()
        .path("id");
  }

  private String retrieveAccountHash(String login) {
    return given()
            .header("Authorization", "Bearer " + retrieveAdminToken())
            .header(acceptLanguageHeader)
            .when()
            .get("/account/login/" + login)
            .then()
            .statusCode(200)
            .extract()
            .path("hash");
  }

  private Header acceptLanguageHeader = new Header("Accept-Language", "en-US");

  @Nested
  @Order(1)
  class Login {
    @Test
    @Order(1)
    void shouldProperlyLoginTest() {
      given()
          .contentType("application/json")
          .header(acceptLanguageHeader)
          .body("""
                     {
                         "login": "administrator",
                         "password": "Student123!"
                     }
              """)
          .when()
          .post("/account/login")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("token", notNullValue())
          .body("token", not(emptyString()));
    }

    @Test
    @Order(2)
    void shouldFailToLoginWhenPasswordIsInvalidTest() {
      given()
          .contentType("application/json")
          .body("""
                     {
                         "login": "admin",
                         "password": "invalid"
                     }
              """)
          .when()
          .post("/account/login")
          .then()
          .statusCode(401)
          .contentType("application/json")
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CREDENTIALS));
    }

    @Test
    @Order(3)
    void shouldFailToLoginWhenLoginDoesNotMatchPasswordTest() {
      given()
          .contentType("application/json")
          .body("""
                     {
                         "login": "invalidLogin",
                         "password": "kochamssbd"
                     }
              """)
          .when()
          .post("/account/login")
          .then()
          .statusCode(401)
          .contentType("application/json")
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CREDENTIALS));
    }

    @Test
    @Order(4)
    void shouldFailToLoginWhenPasswordIsMissingTest() {
      given()
          .contentType("application/json")
          .body("""
                     {
                         "login": "admin"
                     }
              """)
          .when()
          .post("/account/login")
          .then()
          .statusCode(400)
          .contentType("application/json")
          .body("errors", hasSize(1))
          .body("errors[0].class", equalTo("UserCredentialsDto"))
          .body("errors[0].field", equalTo("password"))
          .body("errors[0].message", equalTo("must not be blank"));
    }

    @Test
    @Order(5)
    void shouldFailToLoginWhenLoginIsMissingTest() {
      given()
          .contentType("application/json")
          .body("""
                     {
                         "password": "kochamssbd"
                     }
              """)
          .when()
          .post("/account/login")
          .then()
          .statusCode(400)
          .contentType("application/json")
          .body("errors", hasSize(1))
          .body("errors[0].class", equalTo("UserCredentialsDto"))
          .body("errors[0].field", equalTo("login"))
          .body("errors[0].message", equalTo("must not be blank"));
    }

    @Test
    @Order(6)
    void shouldFailToLoginWhenBodyIsEmptyJsonTest() {
      given()
          .contentType("application/json")
          .body("""
                     {
                     }
              """)
          .when()
          .post("/account/login")
          .then()
          .statusCode(400)
          .contentType("application/json")
          .body("errors", hasSize(2))
          .body("errors.class", hasItems("UserCredentialsDto", "UserCredentialsDto"))
          .body("errors.field", hasItems("password", "login"))
          .body("errors.message", hasItems("must not be blank", "must not be blank"));
    }

    @Test
    @Order(7)
    void shouldFailToLoginWhenBodyIsEmptyTest() {
      given()
          .contentType("application/json")
          .body("")
          .when()
          .post("/account/login")
          .then()
          .statusCode(400)
          .contentType("text/html");
    }

    @Test
    @Order(8)
    void shouldFailToLoginWhenBodyIsMissingTest() {
      given()
          .contentType("application/json")
          .when()
          .post("/account/login")
          .then()
          .statusCode(400)
          .contentType("text/html");
    }
  }

  @Nested
  @Order(2)
  class GetAccountByLogin {
    @Test
    void shouldProperlyGetAccountByLoginTest() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .get("/account/login/administrator")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("accountState", equalTo("ACTIVE"))
          .body("roles", hasSize(1))
          .body("roles[0]", equalTo("administrator"))
          .body("archive", equalTo(false))
          .body("email", equalTo("admin@gmail.com"))
          .body("id", is(notNullValue()))
          .body("locale", equalTo("pl"))
          .body("login", equalTo("administrator"));
    }

    @Test
    void shouldFailToGetAccountByLoginWithoutTokenTest() {
      given()
          .when()
          .get("/account/login/admin")
          .then()
          .statusCode(401)
          .contentType("text/html");
    }

    @Test
    void shouldFailToGetAccountByLoginWhenLoginDoesNotExist() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .get("/account/login/invalid")
          .then()
          .statusCode(404)
          .contentType("application/json")
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }
  }

  @Nested
  @Order(4)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class blockAccount {
// fixme all block and activate tests need to be independent

//    @Test
//    @Order(1)
//    void shouldProperlyBlockActiveAccount() {
//      int id = retrieveAccountId("Active123");
//      given()
//          .header("Authorization", "Bearer " + retrieveAdminToken())
//          .patch("/account/block/" + id)
//          .then()
//          .statusCode(200);
//    }
//
//    @Test
//    @Order(2)
//    void shouldFailToBlockAlreadyBlockedAccount() {
//      int id = retrieveAccountId("Active123");
//      given()
//              .header("Authorization", "Bearer " + retrieveAdminToken())
//              .patch("/account/block/" + id)
//              .then()
//              .statusCode(400)
//              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CHANGE_STATE));
//    }

//    @Test
//    @Order(2)
//    void shouldFailToBlockInactiveAccount() {
//      int id = retrieveAccountId("inactive123");
//      given()
//              .header("Authorization", "Bearer " + retrieveAdminToken())
//              .patch("/account/block/" + id)
//              .then()
//              .statusCode(400)
//              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CHANGE_STATE));
//    }
//    fixme I think we can't create inactive account now

//    @Test
//    @Order(3)
//    void shouldFailToBlockNotVerifiedAccount() {
//      int id = retrieveAccountId("notverified123");
//      given()
//              .header("Authorization", "Bearer " + retrieveAdminToken())
//              .patch("/account/block/" + id)
//              .then()
//              .statusCode(400)
//              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CHANGE_STATE));
//    }
//    fixme same here

//    @Test
//    @Order(4)
//    void shouldFailToBlockNotExistingAccount() {
//      given()
//              .header("Authorization", "Bearer " + retrieveAdminToken())
//              .patch("/account/block/" + Long.MAX_VALUE)
//              .then()
//              .statusCode(404)
//              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
//    }

  }

  @Nested
  @Order(5)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class activateAccount {

//    @Test
//    @Order(1)
//    void shouldFailToActivateAlreadyActiveAccount() {
//      int id = retrieveAccountId("administrator");
//      given()
//          .header("Authorization", "Bearer " + retrieveAdminToken())
//          .patch("/account/activate/" + id)
//          .then()
//          .statusCode(400)
//          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CHANGE_STATE));
//    }
//
//    @Test
//    @Order(2)
//    void shouldFailToActivateInactiveAccount() {
//      int id = retrieveAccountId("administrator");
//      given()
//          .header("Authorization", "Bearer " + retrieveAdminToken())
//          .patch("/account/activate/" + id)
//          .then()
//          .statusCode(400)
//          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CHANGE_STATE));
//    }
//
//    @Test
//    @Order(3)
//    void shouldFailToActivateNotExistingAccount() {
//      given()
//          .header("Authorization", "Bearer " + retrieveAdminToken())
//          .patch("/account/activate/" + Long.MAX_VALUE)
//          .then()
//          .statusCode(404)
//          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
//    }

//    @Test
//    @Order(4)
//    void shouldProperlyActivateNotVerifiedAccount() {
//      int id = retrieveAccountId("notverified123");
//      given()
//          .header("Authorization", "Bearer " + retrieveAdminToken())
//          .patch("/account/activate/" + id)
//          .then()
//          .statusCode(200);
//    }
//    fixme account after creation from admin is always Active

//    @Test
//    @Order(5)
//    void shouldProperlyActivateBlockedAccount() {
//      int id = retrieveAccountId("Active123");
//      given()
//          .header("Authorization", "Bearer " + retrieveAdminToken())
//          .patch("/account/activate/" + id)
//          .then()
//          .statusCode(200);
//    }
  }

  @Nested
  @Order(6)
  class GetAccountByAccountId {
    @Test
    void shouldProperlyGetAccountByAccountIdTest() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .get("/account/id/" + retrieveAccountId("administrator"))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("accountState", equalTo("ACTIVE"))
          .body("roles", hasSize(1))
          .body("roles[0]", equalTo("administrator"))
          .body("archive", equalTo(false))
          .body("email", equalTo("admin@gmail.com"))
          .body("id", is(notNullValue()))
          .body("locale", equalTo("pl"))
          .body("login", equalTo("administrator"));
    }

    @Test
    void shouldFailToGetAccountByAccountIdWithoutTokenTest() {
      given()
          .when()
          .get("/account/id/555")
          .then()
          .statusCode(401)
          .contentType("text/html");
    }

    @Test
    void shouldFailToGetAccountByAccountIdNoneIdGiven() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .get("/account/id")
          .then()
          .statusCode(404)
          .contentType("text/html");
    }

    @Test
    void shouldFailToGetAccountByAccountIdWhenAccountIdDoesNotExist() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .get("/account/id/555")
          .then()
          .statusCode(404)
          .contentType("application/json")
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }
  }

  @Nested
  @Order(7)
  class GetAllAccounts {
    @Test
    void shouldProperlyGetAllAccountsTest() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .get("/account")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("size()", is(greaterThan(0)));
    }

    @Test
    void shouldFailToGetAllAccountsWithoutTokenTest() {
      given()
          .when()
          .get("/account")
          .then()
          .statusCode(401)
          .contentType("text/html");
    }
  }

  @Nested
  @Order(8)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class AddAccessLevelToAccount {
    @Test
    @Order(1)
    void shouldProperlyCreateAccountToEditAccessLevels() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .contentType("application/json")
          .body(InitData.accountToEditAccessLevelsJson)
          .when()
          .post("/account/create")
          .then()
          .statusCode(201);
    }

    @Test
    @Order(2)
    void shouldProperlyAddAccessLevelToAccount() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .get("/account/id/" + retrieveAccountId("accounttoeditals"))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("roles[0]", equalTo("employee"));

      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .get("/account/id/" + retrieveAccountId("accounttoeditals"))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("roles[0]", equalTo("employee"));
    }

    @Test
    @Order(3)
    void failsToAddAccessLevelWithoutAuthorizationToken() {
      given()
          .when()
          .put("/account/id/" + retrieveAccountId("administrator") + "/accessLevel/SalesRep")
          .then()
          .statusCode(401);
    }

    @Test
    @Order(4)
    void failsToAddAccessLevelWhenAccountDoesNotExist() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .put("account/id/0/accessLevel/SalesRep")
          .then()
          .statusCode(404)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }

    @Test
    @Order(5)
    void failsToAddAccessLevelWhenAccessLevelIsInvalid() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .put("/account/id/" + retrieveAccountId("accounttoeditals") + "/accessLevel/NotValidAccessLevel")
          .then()
          .statusCode(400)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL));
    }

    @Test
    @Order(6)
    void failsToAddAccessLevelWhenAccessLevelIsAlreadyAssigned() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .put("/account/id/" + retrieveAccountId("accounttoeditals") + "/accessLevel/Employee")
          .then()
          .statusCode(400)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL_ALREADY_ASSIGNED));
    }
  }

  @Nested
  @Order(9)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class RemoveAccessLevelFromAccount {
    @Test
    @Order(1)
    void failsToRemoveAccessLevelWithoutAuthorizationToken() {
      given()
          .when()
          .delete("/account/id/" + retrieveAccountId("accounttoeditals") + "/accessLevel/Administrator")
          .then()
          .statusCode(401);
    }

    @Test
    @Order(2)
    void failsToRemoveAccessLevelWhenAccountDoesNotExist() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .put("account/id/0/accessLevel/Administrator")
          .then()
          .statusCode(404)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }

    @Test
    @Order(3)
    void failsToRemoveAccessLevelWhenAccessLevelIsInvalid() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .delete("/account/id/" + retrieveAccountId("accounttoeditals") + "/accessLevel/NotValidAccessLevel")
          .then()
          .statusCode(400)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL));
    }

    @Test
    @Order(4)
    void failsToRemoveAccessLevelWhenAccessLevelIsNotAssigned() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .delete("/account/id/" + retrieveAccountId("accounttoeditals") + "/accessLevel/Administrator")
          .then()
          .statusCode(400)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_REMOVE_ACCESS_LEVEL));
    }
  }

  @Nested
  @Order(10)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class EditOwnAccount {

    @Test
    @Order(1)
    void shouldProperlyCreateAccountToEdit() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .contentType("application/json")
          .body(InitData.accountToEditJson)
          .when()
          .post("/account/create")
          .then()
          .statusCode(201);
    }

    @Test
    @Order(2)
    void editOwnAccount() {
      InitData.editedAccountExampleJson =
              InitData.editedAccountAsAdminExampleJson
                      .replace("$hash", retrieveAccountHash("accounttoedit123"));
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .contentType("application/json")
          .body(InitData.editedAccountExampleJson)
          .when()
          .put("/account/login/accounttoedit123/editOwnAccount")
          .then()
          .statusCode(200)
          .contentType("application/json");
    }

    @Test
    @Order(3)
    void failsIfGivenInvalidLogin() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .contentType("application/json")
          .body(InitData.editedAccountAsAdminExampleJson)
          .when()
          .put("/account/login/invalidLogin/editOwnAccount")
          .then()
          .statusCode(404)
          .contentType("application/json")
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }

  }

  @Nested
  @Order(11)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class EditAccountAsAdmin {

    @Test
    @Order(1)
    void editOtherUserAsAdmin() {
      InitData.editedAccountAsAdminExampleJson =
              InitData.editedAccountAsAdminExampleJson
                      .replace("$hash", retrieveAccountHash("accounttoedit123"));
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .contentType("application/json")
          .body(InitData.editedAccountAsAdminExampleJson)
          .when()
          .put("/account/login/accounttoedit123/editAccountAsAdmin")
          .then()
          .statusCode(200)
          .contentType("application/json");
    }

    @Test
    @Order(2)
    void failsIfGivenInvalidLogin() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .contentType("application/json")
          .body(InitData.editedAccountAsAdminExampleJson)
          .when()
          .put("/account/login/invalidLogin/editAccountAsAdmin")
          .then()
          .statusCode(404)
          .contentType("application/json")
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }
  }
  @Nested
  @Order(12)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class getOwnAccountInformation {
    @Test
    @Order(1)
    void administratorShouldProperlyGetInformationAboutOwnAccount() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .get("/account/self")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("id", is(equalTo(retrieveAccountId("administrator"))))
          .body("login", is(equalTo("administrator")))
          .body("email", is(equalTo("admin@gmail.com")))
          .body("firstName", is(equalTo("Jan")))
          .body("lastName", is(equalTo("Kowalski")))
          .body("archive", is(equalTo(false)))
          .body("lastLogin", is(notNullValue()))
          .body("lastFailedLogin", is(nullValue()))
          .body("lastLoginIpAddress", is(notNullValue()))
          .body("lastFailedLoginIpAddress", is(nullValue()))
          .body("locale", is(equalTo("pl")))
          .body("failedLoginCounter", is(equalTo(0)))
          .body("blockadeEnd", is(nullValue()))
          .body("accountState", is(equalTo("ACTIVE")))
          .body("roles.size()", is(equalTo(1)))
          .body("roles[0]", is(equalTo("administrator")))
          .body("address", is(notNullValue()))
          .body("address.country", is(equalTo("Poland")))
          .body("address.city", is(equalTo("Lodz")))
          .body("address.street", is(equalTo("Aleje Testowe")))
          .body("address.postalCode", is(equalTo("93-590")))
          .body("address.streetNumber", is(equalTo(55)));
    }

    @Test
    @Order(2)
    void clientShouldProperlyGetInformationAboutOwnAccount() {
      given()
          .header("Authorization", "Bearer " + retrieveClientToken())
          .when()
          .get("/account/self")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("id", is(equalTo(retrieveAccountId("client"))))
          .body("login", is(equalTo("client")))
          .body("email", is(equalTo("adam.mickiewicz@gmail.com")))
          .body("firstName", is(equalTo("Adam")))
          .body("lastName", is(equalTo("Mickiewicz")))
          .body("archive", is(equalTo(false)))
          .body("lastLogin", is(notNullValue()))
          .body("lastFailedLogin", is(nullValue()))
          .body("lastLoginIpAddress", is(notNullValue()))
          .body("lastFailedLoginIpAddress", is(nullValue()))
          .body("locale", is(equalTo("pl")))
          .body("failedLoginCounter", is(equalTo(0)))
          .body("blockadeEnd", is(nullValue()))
          .body("accountState", is(equalTo("ACTIVE")))
          .body("roles.size()", is(equalTo(1)))
          .body("roles[0]", is(equalTo("client")))
          .body("address", is(notNullValue()))
          .body("address.country", is(equalTo("Poland")))
          .body("address.city", is(equalTo("Lodz")))
          .body("address.street", is(equalTo("Przybyszewskiego")))
          .body("address.postalCode", is(equalTo("93-116")))
          .body("address.streetNumber", is(equalTo(13)));
    }
  }

}
