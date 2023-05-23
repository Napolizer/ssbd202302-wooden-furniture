package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static pl.lodz.p.it.ssbd2023.ssbd02.web.InitData.retrieveAdminToken;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.16 - View user accounts")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK16IT {
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Positive {
        @Test
        @Order(1)
        @DisplayName("Should properly get all accounts")
        void shouldProperlyGetAllAccountsTest() {
            String adminToken = retrieveAdminToken();
            given()
                    .header("Authorization", "Bearer " + adminToken)
                    .when()
                    .get("/account")
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("size()", is(greaterThan(0)));
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Negative {
        @Test
        @Order(1)
        @DisplayName("Should fail when trying to get all accounts without admin token")
        void shouldFailToGetAllAccountsWithoutAdminTokenTest() {
          given()
              .when()
              .get("/account")
              .then()
              .statusCode(401)
              .contentType("text/html");
        }
    }
}

