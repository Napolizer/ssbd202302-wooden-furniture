package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.*;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessLevelAlreadyAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class AccountControllerIT {

    private String retrieveAdminToken() {
        return given()
                .contentType("application/json")
                .body("""
                        {
                            "login": "admin",
                            "password": "kochamssbd"
                        }
                 """)
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
                .when()
                .get("/account/login/" + login)
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    @Nested
    @Order(1)
    class Login {
        @Test
        @Order(1)
        public void shouldProperlyLoginTest() {
            given()
                    .contentType("application/json")
                    .body("""
                        {
                            "login": "admin",
                            "password": "kochamssbd"
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
        public void shouldFailToLoginWhenPasswordIsInvalidTest() {
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
                    .body("error", equalTo("Invalid credentials"));
        }

        @Test
        @Order(3)
        public void shouldFailToLoginWhenLoginDoesNotMatchPasswordTest() {
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
                    .body("error", equalTo("Invalid credentials"));
        }

        @Test
        @Order(4)
        public void shouldFailToLoginWhenPasswordIsMissingTest() {
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
        public void shouldFailToLoginWhenLoginIsMissingTest() {
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
        public void shouldFailToLoginWhenBodyIsEmptyJsonTest() {
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
        public void shouldFailToLoginWhenBodyIsEmptyTest() {
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
        public void shouldFailToLoginWhenBodyIsMissingTest() {
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
        public void shouldProperlyGetAccountByLoginTest() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .when()
                    .get("/account/login/admin")
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("accountState", equalTo("ACTIVE"))
                    .body("groups", hasSize(1))
                    .body("groups[0]", equalTo("ADMINISTRATORS"))
                    .body("archive", equalTo(false))
                    .body("email", equalTo("admin@gmail.com"))
                    .body("id", is(notNullValue()))
                    .body("locale", equalTo("pl"))
                    .body("login", equalTo("admin"));
        }

        @Test
        public void shouldFailToGetAccountByLoginWithoutTokenTest() {
            given()
                    .when()
                    .get("/account/login/admin")
                    .then()
                    .statusCode(401)
                    .contentType("text/html");
        }

        @Test
        public void shouldFailToGetAccountByLoginWhenLoginDoesNotExist() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .when()
                    .get("/account/login/invalid")
                    .then()
                    .statusCode(404)
                    .contentType("application/json")
                    .body("error", equalTo("Account not found"));
        }
    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class createAccount {
        @Test
        @Order(1)
        public void shouldProperlyCreateActiveUser() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .contentType("application/json")
                    .body(InitData.activeAccountJson)
                    .when()
                    .post("/account/create")
                    .then()
                    .statusCode(201);
        }

        @Test
        @Order(2)
        public void shouldProperlyCreateInactiveUser() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .contentType("application/json")
                    .body(InitData.inactiveAccountJson)
                    .when()
                    .post("/account/create")
                    .then()
                    .statusCode(201);
        }

        @Test
        @Order(3)
        public void shouldProperlyCreateBlockedUser() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .contentType("application/json")
                    .body(InitData.blockedAccountJson)
                    .when()
                    .post("/account/create")
                    .then()
                    .statusCode(201);
        }

        @Test
        @Order(4)
        public void shouldProperlyCreateNotVerifiedUser() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .contentType("application/json")
                    .body(InitData.notVerifiedAccountJson)
                    .when()
                    .post("/account/create")
                    .then()
                    .statusCode(201);
        }

        @Test
        @Order(5)
        public void shouldFailToCreateAccountWithSameLogin() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .contentType("application/json")
                    .body(InitData.sameLoginAccountJson)
                    .when()
                    .post("/account/create")
                    .then()
                    .statusCode(400)
                    .body("error", equalTo(new LoginAlreadyExistsException().getMessage()));
        }

        @Test
        @Order(6)
        public void shouldFailToCreateAccountWithSameEmail() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .contentType("application/json")
                    .body(InitData.sameEmailAccountJson)
                    .when()
                    .post("/account/create")
                    .then()
                    .statusCode(400)
                    .body("error", equalTo(new EmailAlreadyExistsException().getMessage()));
        }

        @Test
        @Order(7)
        public void shouldFailToCreateAccountWithInvalidAccessLevel() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .contentType("application/json")
                    .body(InitData.invalidAccessLevelAccountJson)
                    .when()
                    .post("/account/create")
                    .then()
                    .statusCode(400)
                    .body("error", equalTo(new InvalidAccessLevelException().getMessage()));
        }
    }

    @Nested
    @Order(4)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class blockAccount {

        @Test
        @Order(1)
        public void shouldFailToBlockAlreadyBlockedAccount() {
            int id = retrieveAccountId("blocked123");
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .patch("/account/block/" + id)
                    .then()
                    .statusCode(400)
                    .body("error", equalTo(new IllegalAccountStateChangeException().getMessage()));
        }

        @Test
        @Order(2)
        public void shouldFailToBlockInactiveAccount() {
            int id = retrieveAccountId("inactive123");
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .patch("/account/block/" + id)
                    .then()
                    .statusCode(400)
                    .body("error", equalTo(new IllegalAccountStateChangeException().getMessage()));
        }

        @Test
        @Order(3)
        public void shouldFailToBlockNotVerifiedAccount() {
            int id = retrieveAccountId("notverified123");
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .patch("/account/block/" + id)
                    .then()
                    .statusCode(400)
                    .body("error", equalTo(new IllegalAccountStateChangeException().getMessage()));
        }

        @Test
        @Order(4)
        public void shouldFailToBlockNotExistingAccount() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .patch("/account/block/" + Long.MAX_VALUE)
                    .then()
                    .statusCode(400)
                    .body("error", equalTo(new AccountNotFoundException().getMessage()));
        }

        @Test
        @Order(5)
        public void shouldProperlyBlockActiveAccount() {
            int id = retrieveAccountId("active123");
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .patch("/account/block/" + id)
                    .then()
                    .statusCode(200);
        }
    }

    @Nested
    @Order(5)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class activateAccount {

        @Test
        @Order(1)
        public void shouldFailToActivateAlreadyActiveAccount() {
            int id = retrieveAccountId("admin");
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .patch("/account/activate/" + id)
                    .then()
                    .statusCode(400)
                    .body("error", equalTo(new IllegalAccountStateChangeException().getMessage()));
        }

        @Test
        @Order(2)
        public void shouldFailToActivateInactiveAccount() {
            int id = retrieveAccountId("inactive123");
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .patch("/account/activate/" + id)
                    .then()
                    .statusCode(400)
                    .body("error", equalTo(new IllegalAccountStateChangeException().getMessage()));
        }

        @Test
        @Order(3)
        public void shouldFailToActivateNotExistingAccount() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .patch("/account/activate/" + Long.MAX_VALUE)
                    .then()
                    .statusCode(400)
                    .body("error", equalTo(new AccountNotFoundException().getMessage()));
        }

        @Test
        @Order(4)
        public void shouldProperlyActivateNotVerifiedAccount() {
            int id = retrieveAccountId("notverified123");
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .patch("/account/activate/" + id)
                    .then()
                    .statusCode(200);
        }

        @Test
        @Order(5)
        public void shouldProperlyActivateBlockedAccount() {
            int id = retrieveAccountId("blocked123");
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
        public void shouldProperlyGetAccountByAccountIdTest() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .when()
                    .get("/account/id/" + retrieveAccountId("admin"))
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("accountState", equalTo("ACTIVE"))
                    .body("groups", hasSize(1))
                    .body("groups[0]", equalTo("ADMINISTRATORS"))
                    .body("archive", equalTo(false))
                    .body("email", equalTo("admin@gmail.com"))
                    .body("id", is(notNullValue()))
                    .body("locale", equalTo("pl"))
                    .body("login", equalTo("admin"));
        }

        @Test
        public void shouldFailToGetAccountByAccountIdWithoutTokenTest() {
            given()
                    .when()
                    .get("/account/id/555")
                    .then()
                    .statusCode(401)
                    .contentType("text/html");
        }

        @Test
        public void shouldFailToGetAccountByAccountIdNoneIdGiven() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .when()
                    .get("/account/id")
                    .then()
                    .statusCode(404)
                    .contentType("text/html");
        }

        @Test
        public void shouldFailToGetAccountByAccountIdWhenAccountIdDoesNotExist() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .when()
                    .get("/account/id/555")
                    .then()
                    .statusCode(404)
                    .contentType("application/json")
                    .body("error", equalTo("Account not found"));
        }
    }

    @Nested
    @Order(7)
    class GetAllAccounts {
        @Test
        public void shouldProperlyGetAllAccountsTest() {
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
        public void shouldFailToGetAllAccountsWithoutTokenTest() {
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
    class AddAccessLevelToAccount {
        @Test
        @Order(1)
        public void shouldProperlyAddAccessLevelToAccount() {
            int id = retrieveAccountId("admin");
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .when()
                    .put("/account/id/" + id + "/accessLevel/SalesRep")
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("groups[0]", equalTo("ADMINISTRATORS"))
                    .body("groups[1]", equalTo("SALES_REPS"));

        }

        @Test
        @Order(2)
        public void failsToAddAccessLevelWithoutAuthorizationToken() {
            int id = retrieveAccountId("admin");
            given()
                    .when()
                    .put("/account/id/" + id + "/accessLevel/SalesRep")
                    .then()
                    .statusCode(401);
        }

        @Test
        @Order(3)
        public void failsToAddAccessLevelWhenAccountDoesNotExist() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .when()
                    .put("account/id/0/accessLevel/SalesRep")
                    .then()
                    .statusCode(404)
                    .body("error", equalTo("Account not found"));
        }

        @Test
        @Order(4)
        public void failsToAddAccessLevelWhenAccessLevelIsInvalid() {
            int id = retrieveAccountId("admin");
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .when()
                    .put("/account/id/" + id + "/accessLevel/NotValidAccessLevel")
                    .then()
                    .statusCode(400)
                    .body("error", equalTo("Given access level is invalid"));
        }

        @Test
        @Order(5)
        public void failsToAddAccessLevelWhenAccessLevelIsAlreadyAssigned() {
            int id = retrieveAccountId("admin");
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .when()
                    .put("/account/id/" + id + "/accessLevel/Administrator")
                    .then()
                    .statusCode(400)
                    .body("error", equalTo(new AccessLevelAlreadyAssignedException().getMessage()));
        }
    }

    @Nested
    @Order(10)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class editOwnAccount {

        @Test
        @Order(1)
        public void shouldProperlyCreateAccountToEdit() {
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
        public void editOwnAccount() {
            given()
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
        public void failsIfGivenInvalidLogin() {
            given()
                    .header("Authorization", "Bearer " + retrieveAdminToken())
                    .when()
                    .put("/account/login/invalidLogin/editOwnAccount")
                    .then()
                    .statusCode(404)
                    .contentType("application/json")
                    .body("error", equalTo("Account not found"));
        }

    }
}
