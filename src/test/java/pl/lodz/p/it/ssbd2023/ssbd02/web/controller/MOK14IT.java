package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.14 - Confirm e-mail address")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK14IT {
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Positive {
        @Test
        @Order(1)
        @DisplayName("Should properly confirm e-mail address")
        void shouldProperlyConfirmEmailAddress() {

            Integer id = AccountUtil.registerUser("accountToConfirmMail");

            given()
                    .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
                    .when()
                    .get("/account/id/" + id)
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("accountState", is(equalTo("NOT_VERIFIED")));

            String token = InitData.generateConfirmEmailToken("accountToConfirmMail");

            given()
                    .queryParam("token", token)
                    .when()
                    .patch("/account/confirm")
                    .then()
                    .statusCode(200);

            given()
                    .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
                    .when()
                    .get("/account/id/" + id)
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("accountState", is(equalTo("ACTIVE")));
        }
    }
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Negative {
        @Test
        @Order(1)
        @DisplayName("Fails to confirm email address with invalid token")
        void shouldFailWhenConfirmingEmailWithInvalidToken() {

            given()
                    .queryParam("token", "tokenn")
                    .when()
                    .patch("/account/confirm")
                    .then()
                    .statusCode(400)
                    .contentType("application/json")
                    .body("message",is(equalTo("exception.invalid.link")));
        }

        @Test
        @Order(2)
        @DisplayName("Fails to confirm email address with null token")
        void shouldFailWhenConfirmingEmailWithNullToken() {

            given()
                    .queryParam("token", "")
                    .when()
                    .patch("/account/confirm")
                    .then()
                    .statusCode(400)
                    .contentType("application/json")
                    .body("message",is(equalTo("exception.invalid.link")));
        }
    }
}
