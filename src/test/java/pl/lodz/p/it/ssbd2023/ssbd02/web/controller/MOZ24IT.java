package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.24 - Get done order list")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ24IT {
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080/api/v1";
    }
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
