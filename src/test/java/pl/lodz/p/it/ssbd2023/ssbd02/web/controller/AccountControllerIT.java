package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class AccountControllerIT {
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
}
