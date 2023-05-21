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
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
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
  @Order(3)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class createAccount {
    @Test
    @Order(1)
    void shouldProperlyCreateActiveAccount() {
      AccountCreateDto account = InitData.getAccountToCreate();
      account.setLogin("Active123");
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .contentType("application/json")
          .body(InitData.mapToJsonString(account))
          .when()
          .post("/account/create")
          .then()
          .statusCode(201);

      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .when()
              .get("/account/login/Active123")
              .then()
              .statusCode(200)
              .contentType("application/json")
              .body("accountState", equalTo("ACTIVE"))
              .body("roles", hasSize(1))
              .body("roles[0]", equalTo("administrator"));
    }

    @Test
    @Order(2)
    void shouldProperlyCreateAccountWithClientAccessLevel() {
      AccountCreateDto account = InitData.getAccountToCreate();
      account.setLogin("Client123");
      account.setEmail("client123@example.com");
      account.setNip("9999999999");
      account.setAccessLevel(new AccessLevelDto("client"));
      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/create")
              .then()
              .statusCode(201);

      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .when()
              .get("/account/login/Client123")
              .then()
              .statusCode(200)
              .contentType("application/json")
              .body("accountState", equalTo("ACTIVE"))
              .body("roles", hasSize(1))
              .body("roles[0]", equalTo("client"));
    }

    @Test
    @Order(3)
    void shouldProperlyCreateAccountWithEmployeeAccessLevel() {
      AccountCreateDto account = InitData.getAccountToCreate();
      account.setLogin("Employee123");
      account.setEmail("employee123@example.com");
      account.setAccessLevel(new AccessLevelDto("employee"));
      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/create")
              .then()
              .statusCode(201);

      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .when()
              .get("/account/login/Employee123")
              .then()
              .statusCode(200)
              .contentType("application/json")
              .body("accountState", equalTo("ACTIVE"))
              .body("roles", hasSize(1))
              .body("roles[0]", equalTo("employee"));
    }

    @Test
    @Order(4)
    void shouldProperlyCreateAccountWithAdministratorAccessLevel() {
      AccountCreateDto account = InitData.getAccountToCreate();
      account.setLogin("Administrator123");
      account.setEmail("administrator123@example.com");
      account.setAccessLevel(new AccessLevelDto("administrator"));
      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/create")
              .then()
              .statusCode(201);

      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .when()
              .get("/account/login/Administrator123")
              .then()
              .statusCode(200)
              .contentType("application/json")
              .body("accountState", equalTo("ACTIVE"))
              .body("roles", hasSize(1))
              .body("roles[0]", equalTo("administrator"));
    }

    @Test
    @Order(5)
    void shouldProperlyCreateAccountWithSalesRepAccessLevel() {
      AccountCreateDto account = InitData.getAccountToCreate();
      account.setLogin("SalesRep123");
      account.setEmail("salesrep123@example.com");
      account.setAccessLevel(new AccessLevelDto("sales_rep"));
      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/create")
              .then()
              .statusCode(201);

      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .when()
              .get("/account/login/SalesRep123")
              .then()
              .statusCode(200)
              .contentType("application/json")
              .body("accountState", equalTo("ACTIVE"))
              .body("roles", hasSize(1))
              .body("roles[0]", equalTo("sales_rep"));
    }

    @Test
    @Order(6)
    void shouldFailToCreateAccountWithSameLogin() {
      AccountCreateDto account = InitData.getAccountToCreate();
      account.setLogin("Active123");
      account.setEmail("another123@example.com");
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .contentType("application/json")
          .body(InitData.mapToJsonString(account))
          .when()
          .post("/account/create")
          .then()
          .statusCode(409)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_LOGIN_ALREADY_EXISTS));
    }

    @Test
    @Order(7)
    void shouldFailToCreateAccountWithSameEmail() {
      AccountCreateDto account = InitData.getAccountToCreate();
      account.setLogin("Another123");
      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/create")
              .then()
              .statusCode(409)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_EMAIL_ALREADY_EXISTS));
    }

    @Test
    @Order(8)
    void shouldFailToCreateAccountWithSameCompanyNip() {
      AccountCreateDto account = InitData.getAccountToCreate();
      account.setLogin("Another123");
      account.setEmail("another123@example.com");
      account.setNip("9999999999");
      account.setAccessLevel(new AccessLevelDto("client"));
      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/create")
              .then()
              .statusCode(409)
              .body("message", equalTo(MessageUtil.MessageKey.COMPANY_NIP_ALREADY_EXISTS));
    }

    @Test
    @Order(9)
    void shouldFailToCreateAccountWithInvalidAccessLevel() {
      AccountCreateDto account = InitData.getAccountToCreate();
      account.setAccessLevel(new AccessLevelDto("invalid"));
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .contentType("application/json")
          .body(InitData.mapToJsonString(account))
          .when()
          .post("/account/create")
          .then()
          .statusCode(400)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL));
    }

    @Test
    @Order(10)
    void shouldFailToCreateAccountAsClient() {
      AccountCreateDto account = InitData.getAccountToCreate();
      given()
              .header("Authorization", "Bearer " + retrieveClientToken())
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/create")
              .then()
              .statusCode(403);
    }
  }

  @Nested
  @Order(4)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class blockAccount {

    @Test
    @Order(1)
    void shouldProperlyBlockActiveAccount() {
      int id = retrieveAccountId("Active123");
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .patch("/account/block/" + id)
          .then()
          .statusCode(200);
    }

    @Test
    @Order(2)
    void shouldFailToBlockAlreadyBlockedAccount() {
      int id = retrieveAccountId("Active123");
      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .patch("/account/block/" + id)
              .then()
              .statusCode(400)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CHANGE_STATE));
    }

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

    @Test
    @Order(4)
    void shouldFailToBlockNotExistingAccount() {
      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .patch("/account/block/" + Long.MAX_VALUE)
              .then()
              .statusCode(404)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }

  }

  @Nested
  @Order(5)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class activateAccount {

    @Test
    @Order(1)
    void shouldFailToActivateAlreadyActiveAccount() {
      int id = retrieveAccountId("administrator");
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .patch("/account/activate/" + id)
          .then()
          .statusCode(400)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CHANGE_STATE));
    }

    @Test
    @Order(2)
    void shouldFailToActivateInactiveAccount() {
      int id = retrieveAccountId("administrator");
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .patch("/account/activate/" + id)
          .then()
          .statusCode(400)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_CHANGE_STATE));
    }

    @Test
    @Order(3)
    void shouldFailToActivateNotExistingAccount() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .patch("/account/activate/" + Long.MAX_VALUE)
          .then()
          .statusCode(404)
          .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
    }

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

    @Test
    @Order(5)
    void shouldProperlyActivateBlockedAccount() {
      int id = retrieveAccountId("Active123");
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .patch("/account/activate/" + id)
          .then()
          .statusCode(200);
    }
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
          .put("/account/id/" + retrieveAccountId("administrator") + "/accessLevel/Sales_Rep")
          .then()
          .statusCode(401);
    }

    @Test
    @Order(4)
    void failsToAddAccessLevelWhenAccountDoesNotExist() {
      given()
          .header("Authorization", "Bearer " + retrieveAdminToken())
          .when()
          .put("account/id/0/accessLevel/Sales_Rep")
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

  @Nested
  @Order(13)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class registerAccount {
    @Test
    @Order(1)
    void shouldProperlyRegisterClientAccount() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setLogin("Register123");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(201);

      given()
              .header("Authorization", "Bearer " + retrieveAdminToken())
              .when()
              .get("/account/login/Register123")
              .then()
              .statusCode(200)
              .contentType("application/json")
              .body("accountState", equalTo("NOT_VERIFIED"))
              .body("roles", hasSize(1))
              .body("roles[0]", equalTo("client"));
    }

    @Test
    @Order(2)
    void shouldProperlyRegisterClientAccountWithCompany() {
      AccountRegisterDto account = InitData.getAccountWithCompany();
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(201);
    }

    @Test
    @Order(3)
    void shouldFailToRegisterAccountWithExistingCompanyNip() {
      AccountRegisterDto account = InitData.getAccountWithCompany();
      account.setEmail("different123@example.com");
      account.setLogin("Different123");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(409)
              .body("message", equalTo(MessageUtil.MessageKey.COMPANY_NIP_ALREADY_EXISTS));
    }

    @Test
    @Order(4)
    void shouldFailToRegisterAccountWithExistingEmail() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setLogin("Different123");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(409)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_EMAIL_ALREADY_EXISTS));
    }


    @Test
    @Order(5)
    void shouldFailToRegisterAccountWithExistingLogin() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setEmail("different123@example.com");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(409)
              .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_LOGIN_ALREADY_EXISTS));
    }

    @Test
    @Order(6)
    void shouldFailToRegisterAccountWithEmptyData() {
      AccountRegisterDto account = AccountRegisterDto.builder().build();
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(11));
    }

    @Test
    @Order(7)
    void shouldFailToRegisterAccountWithInvalidEmailFormat() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setEmail("invalidEmail");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("email"))
              .body("errors[0].message", equalTo("Invalid email address format"));
    }

    @Test
    @Order(8)
    void shouldFailToRegisterAccountWithInvalidLoginPattern() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setLogin("login123_");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("login"))
              .body("errors[0].message",
                      equalTo("Login must start with a letter and contain only letters and digits."));

      account.setLogin("Login_123");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("login"))
              .body("errors[0].message",
                      equalTo("Login must start with a letter and contain only letters and digits."));

      account.setLogin("123Login");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("login"))
              .body("errors[0].message",
                      equalTo("Login must start with a letter and contain only letters and digits."));
    }

    @Test
    @Order(9)
    void shouldFailToRegisterAccountWithTooShortLogin() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setLogin("Joe11");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("login"))
              .body("errors[0].message",
                      equalTo("The length of the field must be between 6 and 20 characters"));

    }

    @Test
    @Order(10)
    void shouldFailToRegisterAccountWithTooLongLogin() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setLogin("Joe11joe11joe11joe11x");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("login"))
              .body("errors[0].message",
                      equalTo("The length of the field must be between 6 and 20 characters"));
    }

    @Test
    @Order(11)
    void shouldFailToRegisterAccountWithWithInvalidPasswordPattern() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setPassword("password123");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("password"))
              .body("errors[0].message", equalTo("Password must contain at least " +
                      "one uppercase letter and one special character from the following set: !@#$%^&+="));

      account.setPassword("password!!!");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("password"))
              .body("errors[0].message", equalTo("Password must contain at least " +
                      "one uppercase letter and one special character from the following set: !@#$%^&+="));

      account.setPassword("password123!");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("password"))
              .body("errors[0].message", equalTo("Password must contain at least " +
                      "one uppercase letter and one special character from the following set: !@#$%^&+="));

      account.setPassword("Password123");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("password"))
              .body("errors[0].message", equalTo("Password must contain at least " +
                      "one uppercase letter and one special character from the following set: !@#$%^&+="));

    }

    @Test
    @Order(12)
    void shouldFailToRegisterAccountWithTooShortPassword() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setPassword("Pass!!!");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("password"))
              .body("errors[0].message",
                      equalTo("The length of the field must be between 8 and 32 characters"));
    }

    @Test
    @Order(13)
    void shouldFailToRegisterAccountWithTooLongPassword() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setPassword("Pass123!Pass123!Pass123!Pass123!!");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("password"))
              .body("errors[0].message",
                      equalTo("The length of the field must be between 8 and 32 characters"));
    }

    @Test
    @Order(14)
    void shouldFailToRegisterAccountWithWithInvalidCapitalizedPattern() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setFirstName("joe");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("firstName"))
              .body("errors[0].message",
                      equalTo("Field must start with a capital letter and contain only letters"));

      account.setFirstName("Joe123");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("firstName"))
              .body("errors[0].message",
                      equalTo("Field must start with a capital letter and contain only letters"));

      account.setFirstName("Joe!");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("firstName"))
              .body("errors[0].message",
                      equalTo("Field must start with a capital letter and contain only letters"));
    }

    @Test
    @Order(15)
    void shouldFailToRegisterAccountWithEmptyCapitalized() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setFirstName("");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(2))
              .body("errors[0].field", equalTo("firstName"))
              .body("errors[1].field", equalTo("firstName"));

    }

    @Test
    @Order(16)
    void shouldFailToRegisterAccountWithTooLongCapitalized() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setFirstName("John DoeBo John DoeBo");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("firstName"))
              .body("errors[0].message",
                      equalTo("The length of the field must be between 1 and 20 characters"));
    }

    @Test
    @Order(17)
    void shouldFailToRegisterAccountWithInvalidLocalePattern() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setLocale("pl_PL");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("locale"))
              .body("errors[0].message",
                      equalTo("Locale must contain only two lowercase letters"));

      account.setLocale("PL");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("locale"))
              .body("errors[0].message",
                      equalTo("Locale must contain only two lowercase letters"));

      account.setLocale("pl_pl");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("locale"))
              .body("errors[0].message",
                      equalTo("Locale must contain only two lowercase letters"));
    }

    @Test
    @Order(18)
    void shouldFailToRegisterAccountWithInvalidPostalCodePattern() {
      AccountRegisterDto account = InitData.getAccountToRegister();
      account.setPostalCode("90222");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("postalCode"))
              .body("errors[0].message",
                      equalTo("Postal code must be in the format xx-xxx"));

      account.setPostalCode("902-22");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("postalCode"))
              .body("errors[0].message",
                      equalTo("Postal code must be in the format xx-xxx"));

      account.setPostalCode("902-222");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("postalCode"))
              .body("errors[0].message",
                      equalTo("Postal code must be in the format xx-xxx"));
    }

    @Test
    @Order(19)
    void shouldFailToRegisterAccountWithInvalidCompanyNipPattern() {
      AccountRegisterDto account = InitData.getAccountWithCompany();
      account.setNip("111111111z");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("nip"))
              .body("errors[0].message",
                      equalTo("NIP must contain 10 digits"));
    }

    @Test
    @Order(20)
    void shouldFailToRegisterAccountWithTooShortCompanyNip() {
      AccountRegisterDto account = InitData.getAccountWithCompany();
      account.setNip("111111111");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("nip"))
              .body("errors[0].message",
                      equalTo("NIP must contain 10 digits"));
    }

    @Test
    @Order(21)
    void shouldFailToRegisterAccountWithTooLongCompanyNip() {
      AccountRegisterDto account = InitData.getAccountWithCompany();
      account.setNip("11111111111");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("nip"))
              .body("errors[0].message",
                      equalTo("NIP must contain 10 digits"));
    }

    @Test
    @Order(22)
    void shouldFailToRegisterAccountWithInvalidCompanyNamePattern() {
      AccountRegisterDto account = InitData.getAccountWithCompany();
      account.setCompanyName("11Company");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("companyName"))
              .body("errors[0].message",
                      equalTo("Field must start with a capital letter"));

      account.setCompanyName("New/Company");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("companyName"))
              .body("errors[0].message",
                      equalTo("Field must start with a capital letter"));

      account.setCompanyName("company123");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("companyName"))
              .body("errors[0].message",
                      equalTo("Field must start with a capital letter"));
    }

    @Test
    @Order(23)
    void shouldFailToRegisterAccountWithEmptyCompanyName() {
      AccountRegisterDto account = InitData.getAccountWithCompany();
      account.setCompanyName("");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(2))
              .body("errors[0].field", equalTo("companyName"))
              .body("errors[1].field", equalTo("companyName"));
    }

    @Test
    @Order(24)
    void shouldFailToRegisterAccountWithTooLongCompanyName() {
      AccountRegisterDto account = InitData.getAccountWithCompany();
      account.setCompanyName("CompanyNCompanyNCompanyNCompanyN1");
      given()
              .contentType("application/json")
              .body(InitData.mapToJsonString(account))
              .when()
              .post("/account/register")
              .then()
              .statusCode(400)
              .body("errors", hasSize(1))
              .body("errors[0].field", equalTo("companyName"))
              .body("errors[0].message",
                      equalTo("The length of the field must be between 1 and 32 characters"));
    }
  }
}
