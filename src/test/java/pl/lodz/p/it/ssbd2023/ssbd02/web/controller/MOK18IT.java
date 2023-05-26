package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("MOK.18 - Send email message when blocking account")
public class MOK18IT {
    @Nested
    @Order(1)
    @TestClassOrder(ClassOrderer.OrderAnnotation.class)
    class Init {
        @Test
        @DisplayName("Should properly create account1")
        @Order(1)
        void shouldProperlyCreateAccount1() {
            given()
                    .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
                    .contentType("application/json")
                    .body(InitData.accountToBlock4Json)
                    .when()
                    .post("/account/create")
                    .then()
                    .statusCode(201);
        }
        @Test
        @DisplayName("Should properly create account2")
        @Order(2)
        void shouldProperlyCreateAccount2() {
            given()
                    .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
                    .contentType("application/json")
                    .body(InitData.accountToBlock5Json)
                    .when()
                    .post("/account/create")
                    .then()
                    .statusCode(201);
        }
        @Test
        @DisplayName("Should properly create account3")
        @Order(3)
        void shouldProperlyCreateAccount3() {
            given()
                    .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
                    .contentType("application/json")
                    .body(InitData.accountToBlock6Json)
                    .when()
                    .post("/account/create")
                    .then()
                    .statusCode(201);
        }
    }
    @Nested
    @Order(2)
    @TestClassOrder(ClassOrderer.OrderAnnotation.class)
    class Positive {
        @DisplayName("Should return successful message when blocking active account")
        @ParameterizedTest(name = "login {0}")
        @CsvSource({
                "blockaccount4",
                "blockaccount5",
                "blockaccount6"
        })
        @Order(1)
        void shouldProperlyReturnSuccessfulMessageWhenBlockingActiveAccount(String login) {
            given()
                    .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
                    .patch("/account/block/" + AccountUtil.getAccountId(login))
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("message", is(equalTo("mok.account.block.successful")));
        }
    }
}
