package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("MOK.15 - Pass email address to send new password")
public class MOK15IT {
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080/api/v1";
    }
    @Nested
    @Order(1)
    class Positive {
        @DisplayName("Should properly send reset password email")
        @ParameterizedTest(name = "email: {0}")
        @CsvSource({
                "admin@gmail.com",
                "adam.mickiewicz@gmail.com",
                "juliusz.slowacki@gmail.com",
                "cyprian.norwid@gmail.com"
        })
        void shouldProperlySendResetPasswordEmail(String email) {
            given()
                    .contentType("application/json")
                    .body("""
                       {
                           "email": "%s"
                       }
                """.formatted(email))
                    .when()
                    .post("/account/forgot-password")
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("message", is(equalTo("reset.password.success")));
        }
    }
        @Nested
        @Order(2)
        @TestClassOrder(ClassOrderer.OrderAnnotation.class)
        class Negative {
            @DisplayName("Should fail if email is in invalid format")
            @ParameterizedTest(name = "invalid email: {0}")
            @CsvSource({
                    "blabla",
                    "123",
                    "@",
                    "null",
                    "abc@",
                    "@123"
            })
            @Order(1)
            void shouldFailIfEmailIsInInvalidFormat(String email) {
                given()
                        .contentType("application/json")
                        .body("""
                         {
                             "email": "%s"
                         }
                  """.formatted(email))
                        .when()
                        .post("/account/forgot-password")
                        .then()
                        .statusCode(400)
                        .contentType("application/json")
                        .body("errors", notNullValue())
                        .body("errors.size()", is(equalTo(1)))
                        .body("errors[0].message", is(equalTo("must be a well-formed email address")));
            }

            @DisplayName("Should fail if email is empty")
            @Test
            @Order(2)
            void shouldFailIfEmailIsEmpty() {
                given()
                        .contentType("application/json")
                        .body("""
                         {
                             "email": ""
                         }
                  """)
                        .when()
                        .post("/account/forgot-password")
                        .then()
                        .statusCode(400)
                        .contentType("application/json")
                        .body("errors", notNullValue())
                        .body("errors.size()", is(equalTo(1)))
                        .body("errors[0].message", is(equalTo("must not be blank")));
            }

            @DisplayName("Should fail to change email in invalid format")
            @ParameterizedTest(name = "invalid email: {0}")
            @CsvSource({
                    "does.not.exist@ssbd.com",
                    "another.non.existing@ssbd.com"
            })
            @Order(3)
            void shouldFailIfEmailDoesNotExist(String email) {
                given()
                        .contentType("application/json")
                        .body("""
                         {
                             "email": "%s"
                         }
                  """.formatted(email))
                        .when()
                        .post("/account/forgot-password")
                        .then()
                        .statusCode(404)
                        .contentType("application/json")
                        .body("message", notNullValue())
                        .body("message", is(equalTo("exception.mok.account.email.does.not.exist")));
            }

            @DisplayName("Should fail if account is inactive")
            @Test
            @Order(4)
            void shouldFailIfAccountIsInactive() {
                int id = AccountUtil.registerUser("inactiveaccount");

                given()
                        .contentType("application/json")
                        .body("""
                         {
                             "email": "inactiveaccount@ssbd.com"
                         }
                  """)
                        .when()
                        .post("/account/forgot-password")
                        .then()
                        .statusCode(400)
                        .contentType("application/json")
                        .body("message", notNullValue())
                        .body("message", is(equalTo("exception.mok.account.not.active")));

                given()
                        .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
                        .when()
                        .patch("/account/activate/" + id)
                        .then()
                        .statusCode(200);
            }
        }
}
