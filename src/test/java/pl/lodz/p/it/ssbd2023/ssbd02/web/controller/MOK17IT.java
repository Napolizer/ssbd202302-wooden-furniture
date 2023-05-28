package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;

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
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.17 - Get other user account info")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK17IT {

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Positive {
        @Test
        @Order(1)
        @DisplayName("Should properly get account by id")
        void shouldProperlyGetAccountByAccountIdTest() {
            int id = AccountUtil.registerUser("accountToGetById");

            given()
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
                    .when()
                    .patch("/account/activate/" + id)
                    .then()
                    .statusCode(200);

            given()
                    .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
                    .when()
                    .get("/account/id/" + id)
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("firstName", is(equalTo("John")))
                    .body("lastName", is(equalTo("Doe")))
                    .body("archive", is(equalTo(false)))
                    .body("locale", is(equalTo("en")))
                    .body("failedLoginCounter", is(equalTo(0)))
                    .body("blockadeEnd", is(nullValue()))
                    .body("accountState", is(equalTo("ACTIVE")))
                    .body("roles.size()", is(equalTo(1)))
                    .body("address", is(notNullValue()))
                    .body("address.country", is(equalTo("Poland")))
                    .body("address.city", is(equalTo("Lodz")))
                    .body("address.street", is(equalTo("Piotrkowska")))
                    .body("address.postalCode", is(equalTo("90-000")))
                    .body("address.streetNumber", is(equalTo(1)));
        }
    }
    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Negative {
        @Test
        @Order(1)
        @DisplayName("Should fail when trying to get account by id without token")
        void shouldFailToGetAccountByAccountIdWithoutTokenTest() {
            given()
                    .when()
                    .get("/account/id/555")
                    .then()
                    .statusCode(401)
                    .contentType("text/html");
        }

        @Test
        @Order(2)
        @DisplayName("Should fail when trying to get account by id with none id")
        void shouldFailToGetAccountByAccountIdNoneIdGiven() {
            given()
                    .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
                    .when()
                    .get("/account/id")
                    .then()
                    .statusCode(404)
                    .contentType("text/html");
        }

        @Test
        @Order(3)
        @DisplayName("Should fail when trying to get account by id that does not exist")
        void shouldFailToGetAccountByAccountIdWhenAccountIdDoesNotExist() {
            given()
                    .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
                    .when()
                    .get("/account/id/555")
                    .then()
                    .statusCode(404)
                    .contentType("application/json")
                    .body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_NOT_FOUND));
        }
    }
}
