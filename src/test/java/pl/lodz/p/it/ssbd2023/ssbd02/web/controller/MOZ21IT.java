package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.DELIVERED;

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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.OrderUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.21 - Observe order")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ21IT {

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {
    @Order(1)
    @DisplayName("Should properly observe created order")
    @Test
    void shouldProperlyObserveCreatedOrder() {
      String token = AuthUtil.retrieveToken("client");
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + token)
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/observe")
          .then()
          .statusCode(200)
          .body("observed", equalTo(true));
    }

    @Order(2)
    @DisplayName("Should properly observe completed order")
    @Test
    void shouldProperlyObserveCompletedOrder() {
      String token = AuthUtil.retrieveToken("client");
      OrderDto order = OrderUtil.createOrder(OrderState.COMPLETED);
      given()
          .header("Authorization", "Bearer " + token)
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/observe")
          .then()
          .statusCode(200)
          .body("observed", equalTo(true));
    }

    @Order(3)
    @DisplayName("Should properly observe in delivery order")
    @Test
    void shouldProperlyObserveInDeliveryOrder() {
      String token = AuthUtil.retrieveToken("client");
      OrderDto order = OrderUtil.createOrder(OrderState.IN_DELIVERY);
      given()
          .header("Authorization", "Bearer " + token)
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/observe")
          .then()
          .statusCode(200)
          .body("observed", equalTo(true));
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {
    @Test
    @DisplayName("should fail to observe delivered order")
    @Order(1)
    void shouldFailToObserveDeliveredOrder() {
      OrderDto order = OrderUtil.createOrder(DELIVERED);
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
          .put("/order/observe")
          .then()
          .statusCode(400)
          .body("message", equalTo("exception.moz.order.delivered"));
    }

    @Test
    @DisplayName("should fail to observe cancelled order")
    @Order(2)
    void shouldFailToObserveCancelledOrder() {
      String token = AuthUtil.retrieveToken("client");
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + token)
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), order.getHash()))
          .when()
          .put("/order/observe")
          .then()
          .statusCode(200)
          .body("observed", equalTo(true));

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
          .put("/order/observe")
          .then()
          .statusCode(400)
          .body("message", equalTo("exception.moz.order.already.observed"));
    }

    @Test
    @DisplayName("should fail to observe order with invalid hash")
    @Order(3)
    void shouldFailToObserveOrderWithInvalidHash() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), "123"))
          .when()
          .put("/order/observe")
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("should fail to observe order without hash")
    @Order(4)
    void shouldFailToObserveOrderWithoutHash() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
            }
            """.formatted(order.getId()))
          .when()
          .put("/order/observe")
          .then()
          .statusCode(not(equalTo(200)));
    }

    @Test
    @DisplayName("should fail to observe order with empty body")
    @Order(5)
    void shouldFailToObserveOrderWithEmptyBody() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client"))
          .header("Content-Type", "application/json")
          .body("""
            {
            }
            """)
          .when()
          .put("/order/observe")
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("should fail to observe order with no body")
    @Order(6)
    void shouldFailToObserveOrderWithNoBody() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client"))
          .header("Content-Type", "application/json")
          .when()
          .put("/order/observe")
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("should fail to observe order without bearer")
    @Order(7)
    void shouldFailToObserveOrderWithoutBearer() {
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
          .put("/order/observe")
          .then()
          .statusCode(401);
    }

    @Test
    @DisplayName("should fail to observe order without client role")
    @Order(8)
    void shouldFailToObserveOrderWithoutClientRole() {
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
          .put("/order/observe")
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
          .put("/order/observe")
          .then()
          .statusCode(403);

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
          .put("/order/observe")
          .then()
          .statusCode(403);

    }
  }
}
