package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.OrderUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.*;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.22 - Cancel own order")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MOZ22IT {

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {
    @Order(1)
    @DisplayName("Should properly cancel created order")
    @Test
    void shouldProperlyCancelCreatedOrder() {
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
          .put("/order/cancel")
          .then()
          .statusCode(200)
          .body("orderState", equalTo("CANCELLED"));
    }

    @Order(2)
    @DisplayName("Should properly cancel completed order")
    @Test
    void shouldProperlyCancelCompletedOrder() {
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
          .put("/order/cancel")
          .then()
          .statusCode(200)
          .body("orderState", equalTo("CANCELLED"));
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {

    @Test
    @DisplayName("should fail to cancel already delivered order")
    @Order(1)
    void shouldFailToCancelAlreadyDeliveredOrder() {
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
          .put("/order/cancel")
          .then()
          .statusCode(400)
          .body("message", equalTo("exception.moz.order.already.delivered"));
    }

    @Test
    @DisplayName("should fail to cancel already cancelled order")
    @Order(2)
    void shouldFailToCancelAlreadyCancelledOrder() {
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
          .put("/order/cancel")
          .then()
          .statusCode(200)
          .body("orderState", equalTo("CANCELLED"));
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
          .put("/order/cancel")
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
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "id": "%s",
              "hash": "%s"
            }
            """.formatted(order.getId(), "123"))
          .when()
          .put("/order/cancel")
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("should fail to cancel order without hash")
    @Order(4)
    void shouldFailToCancelOrderWithoutHash() {
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
          .put("/order/cancel")
          .then()
          .statusCode(not(equalTo(200)));
    }

    @Test
    @DisplayName("should fail to cancel order with empty body")
    @Order(5)
    void shouldFailToCancelOrderWithEmptyBody() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client"))
          .header("Content-Type", "application/json")
          .body("""
            {
            }
            """)
          .when()
          .put("/order/cancel")
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("should fail to cancel order with no body")
    @Order(6)
    void shouldFailToCancelOrderWithNoBody() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client"))
          .header("Content-Type", "application/json")
          .when()
          .put("/order/cancel")
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
          .put("/order/cancel")
          .then()
          .statusCode(401);
    }

    @Test
    @DisplayName("should fail to cancel order without employee role")
    @Order(8)
    void shouldFailToCancelOrderWithoutClientRole() {
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
          .put("/order/cancel")
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
          .put("/order/cancel")
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
          .put("/order/cancel")
          .then()
          .statusCode(403);
    }

    @Test
    @DisplayName("should fail to cancel order in delivery")
    @Order(1)
    void shouldFailToCancelOrderInDelivery() {
      OrderDto order = OrderUtil.createOrder(IN_DELIVERY);
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
          .put("/order/cancel")
          .then()
          .statusCode(400)
          .body("message", equalTo("exception.moz.order.already.in.delivery"));
    }
  }
}
