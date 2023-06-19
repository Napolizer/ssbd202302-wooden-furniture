package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.OrderUtil;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.25 - Filter done orders")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MOZ25IT {
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080/api/v1";
    }
    public OrderDto order;
    @Nested
    @Order(1)
    @TestClassOrder(ClassOrderer.OrderAnnotation.class)
    class Positive {
        @Order(1)
        @DisplayName("Should properly create order")
        @Test
        void shouldProperlyGetCreateOrder() {
            order = OrderUtil.createOrder(OrderState.DELIVERED);
            given()
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(EMPLOYEE))
                    .when()
                    .get("/order/id/%s".formatted(order.getId()))
                    .then()
                    .statusCode(200)
                    .body("id", notNullValue())
                    .body("orderedProducts", notNullValue())
                    .body("orderedProducts.amount", notNullValue())
                    .body("orderedProducts.productId", notNullValue())
                    .body("orderState", equalTo("DELIVERED"))
                    .body("recipientFirstName", equalTo("Jan"))
                    .body("recipientLastName", equalTo("Kowalski"))
                    .body("recipientAddress", notNullValue())
                    .body("recipientAddress.country", equalTo("Poland"))
                    .body("recipientAddress.city", equalTo("Lodz"))
                    .body("recipientAddress.street", equalTo("Piotrkowska"))
                    .body("recipientAddress.streetNumber", equalTo(1))
                    .body("recipientAddress.postalCode", equalTo("90-000"))
                    .body("accountLogin", equalTo("client"))
                    .body("hash", notNullValue())
                    .body("observed", equalTo(false))
                    .body("totalPrice", equalTo(3699.99f));
        }

        @Order(2)
        @DisplayName("Should properly get done order with filters")
        @Test
        void shouldProperlyGetDoneOrderWithFilter() {
            given()
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(SALES_REP))
                    .when()
                    .get("/order/filters?minPrice=3699&maxPrice=3700&amount=1")
                    .then()
                    .contentType(MediaType.APPLICATION_JSON)
                    .statusCode(200)
                    .body("id", notNullValue())
                    .body("orderedProducts", notNullValue())
                    .body("orderedProducts.amount", notNullValue())
                    .body("orderedProducts.productId", notNullValue())
                    .body("orderState", notNullValue())
                    .body("recipientFirstName", notNullValue())
                    .body("recipientLastName", notNullValue())
                    .body("recipientAddress", notNullValue())
                    .body("recipientAddress.country", notNullValue())
                    .body("recipientAddress.city", notNullValue())
                    .body("recipientAddress.street", notNullValue())
                    .body("recipientAddress.streetNumber", notNullValue())
                    .body("recipientAddress.postalCode", notNullValue())
                    .body("accountLogin", notNullValue())
                    .body("hash", notNullValue())
                    .body("observed", notNullValue())
                    .body("totalPrice", notNullValue());
        }
        @Order(3)
        @DisplayName("Should return empty list")
        @Test
        void shouldReturnEmptyList() {
            given()
                    .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(SALES_REP))
                    .when()
                    .get("/order/filters?minPrice=3699&maxPrice=3700&amount=231231")
                    .then()
                    .contentType(MediaType.APPLICATION_JSON)
                    .statusCode(200)
                    .body("size()",is(equalTo(0)));
        }
    }
    @Nested
    @Order(2)
    @TestClassOrder(ClassOrderer.OrderAnnotation.class)
    class Negative {
        @Test
        @DisplayName("Should fail if bearer is missing")
        @Order(1)
        void shouldFailIfBearerIsMissing() {
            given()
                    .when()
                    .get("/order/filters?minPrice=3699&maxPrice=3700&amount=1")
                    .then()
                    .statusCode(401);
        }
    }
}
