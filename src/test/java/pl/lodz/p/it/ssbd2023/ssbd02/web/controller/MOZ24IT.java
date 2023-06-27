package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

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
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.24 - Get done order list")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ24IT {
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Positive {
        @Order(1)
        @DisplayName("Should properly get done orders")
        @Test
        void shouldProperlyGetDoneOrders() {
            given()
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(SALES_REP))
                    .when()
                    .get("/order/done")
                    .then()
                    .statusCode(200)
                    .body("size()", is(greaterThan(0)));
        }
    }

    @Nested
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Negative {
        @Order(1)
        @DisplayName("Should fail to get done orders without token")
        @Test
        void shouldFailToGetDoneOrdersWithoutToken() {
            given()
                    .when()
                    .get("/order/done")
                    .then()
                    .statusCode(401);
        }
    }
}
