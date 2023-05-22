package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

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
            given()
                    .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
                    .when()
                    .get("/account/id/" + InitData.retrieveAccountId("client"))
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
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
