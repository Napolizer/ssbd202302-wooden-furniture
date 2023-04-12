package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccessLevelAlreadyAssignedException;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
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
    class AddAccessLevelToAccount {
        @Test
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
        public void failsToAddAccessLevelWithoutAuthorizationToken() {
            int id = retrieveAccountId("admin");
            given()
                    .when()
                    .put("/account/id/" + id + "/accessLevel/SalesRep")
                    .then()
                    .statusCode(401);
        }

        @Test
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
}
