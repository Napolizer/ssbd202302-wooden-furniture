package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.*;
import static pl.lodz.p.it.ssbd2023.ssbd02.web.InitData.retrieveAdminToken;

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
        void shouldFail() {

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
        @DisplayName("Should fail2")
        void shouldFail2() {

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
