package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.CANCELLED;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.COMPLETED;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.DELIVERED;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.IN_DELIVERY;

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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.OrderUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.11 - Cancel order as employee")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ11IT {
  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Positive {
    @DisplayName("Should properly cancel order as employee")
    @ParameterizedTest(name = "initialState {0}")
    @CsvSource({
        "CREATED",
        "COMPLETED",
    })
    @Order(1)
    void shouldProperlyCancelOrder(OrderState initialState) {
      OrderDto order = OrderUtil.createOrder(initialState);
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(200)
          .body("orderState", equalTo("CANCELLED"));
    }
  }

  @Nested
  @Order(2)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Negative {
    @Test
    @DisplayName("should fail to cancel already delivered order")
    @Order(1)
    void shouldFailToCancelAlreadyDeliveredOrder() {
      OrderDto order = OrderUtil.createOrder(DELIVERED);
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(400)
          .body("message", equalTo("exception.moz.order.already.delivered"));
    }

    @Test
    @DisplayName("should fail to cancel already cancelled order")
    @Order(2)
    void shouldFailToCancelAlreadyCancelledOrder() {
      OrderDto order = OrderUtil.createOrder(COMPLETED);
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(200)
          .body("orderState", equalTo("CANCELLED"));
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(400)
          .body("message", equalTo("exception.moz.order.already.cancelled"));
    }

    @Test
    @DisplayName("should fail to cancel order with invalid hash")
    @Order(3)
    void shouldFailToCancelOrderWithInvalidHash() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), "123"))
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("should fail to cancel order without hash")
    @Order(4)
    void shouldFailToCancelOrderWithoutHash() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
            }
            """.formatted(order.getId()))
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(not(equalTo(200)));
    }

    @Test
    @DisplayName("should fail to cancel order with empty body")
    @Order(5)
    void shouldFailToCancelOrderWithEmptyBody() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
            }
            """)
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("should fail to cancel order with no body")
    @Order(6)
    void shouldFailToCancelOrderWithNoBody() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("should fail to cancel order without bearer")
    @Order(7)
    void shouldFailToCancelOrderWithoutBearer() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(401);
    }

    @Test
    @DisplayName("should fail to cancel order without employee role")
    @Order(8)
    void shouldFailToCancelOrderWithoutEmployeeRole() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(403);
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("sales_rep"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(403);
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(403);
    }

    @Test
    @DisplayName("should fail to cancel order in delivery")
    @Order(1)
    void shouldFailToCancelOrderInDelivery() {
      OrderDto order = OrderUtil.createOrder(IN_DELIVERY);
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/employee/cancel")
          .then()
          .statusCode(400)
          .body("message", equalTo("exception.moz.order.already.in.delivery"));
    }
  }
}
