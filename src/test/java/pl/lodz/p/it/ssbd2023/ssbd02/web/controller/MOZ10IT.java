package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.COMPLETED;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.DELIVERED;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.IN_DELIVERY;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.OrderUtil;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.10 - Change order state")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ10IT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }
  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Positive {
    @DisplayName("Should properly change order state")
    @ParameterizedTest(name = "initialState, newState {0}, {1}")
    @CsvSource({
        "CREATED,COMPLETED",
        "CREATED,IN_DELIVERY",
        "CREATED,DELIVERED",
        "COMPLETED,IN_DELIVERY",
        "COMPLETED,DELIVERED",
        "IN_DELIVERY,DELIVERED",
    })
    @Order(1)
    void shouldProperlyChangeOrderState(OrderState initialState, OrderState newState) {
      OrderDto order = OrderUtil.createOrder(initialState);
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "state": "%s",
              "hash": "%s"
            }
            """.formatted(newState, order.getHash()))
          .when()
          .put("/order/state/%s".formatted(order.getId()))
          .then()
          .statusCode(200)
          .body("orderState", equalTo(newState.toString()));
    }
  }

  @Nested
  @Order(2)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Negative {
    @DisplayName("should fail to change state to forbidden one")
    @ParameterizedTest(name = "initialState, newState {0}, {1}")
    @CsvSource({
        "DELIVERED,IN_DELIVERY",
        "DELIVERED,COMPLETED",
        "DELIVERED,CREATED",
        "IN_DELIVERY,COMPLETED",
        "IN_DELIVERY,CREATED",
        "COMPLETED,CREATED",
    })
    @Order(1)
    void shouldFailToChangeStateToForbiddenOne(OrderState initialState, OrderState newState) {
      OrderDto order = OrderUtil.createOrder(initialState);
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "state": "%s",
              "hash": "%s"
            }
            """.formatted(newState, order.getHash()))
          .when()
          .put("/order/state/%s".formatted(order.getId()))
          .then()
          .statusCode(400)
          .body("message", equalTo("exception.moz.invalid.order.state.transition"));
    }

    @DisplayName("should fail to change state to the same one")
    @ParameterizedTest(name = "initialState {0}")
    @CsvSource({
        "CREATED",
        "COMPLETED",
        "IN_DELIVERY",
        "DELIVERED",
    })
    @Order(2)
    void shouldFailToChangeStateToTheSameOne(OrderState initialState) {
      OrderDto order = OrderUtil.createOrder(initialState);
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "state": "%s",
              "hash": "%s"
            }
            """.formatted(initialState, order.getHash()))
          .when()
          .put("/order/state/%s".formatted(order.getId()))
          .then()
          .statusCode(400)
          .body("message", equalTo("exception.moz.invalid.order.state.transition"));
    }

    @Test
    @DisplayName("should fail to change state with invalid hash")
    @Order(3)
    void shouldFailToChangeStateWithInvalidHash() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "state": "%s",
              "hash": "%s"
            }
            """.formatted(COMPLETED, "123"))
          .when()
          .put("/order/state/%s".formatted(order.getId()))
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("should fail to change state without hash")
    @Order(4)
    void shouldFailToChangeStateWithoutHash() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "state": "%s",
            }
            """.formatted(COMPLETED))
          .when()
          .put("/order/state/%s".formatted(order.getId()))
          .then()
          .statusCode(not(equalTo(200)));
    }

    @Test
    @DisplayName("should fail to change state with empty body")
    @Order(5)
    void shouldFailToChangeStateWithEmptyBody() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
            }
            """)
          .when()
          .put("/order/state/%s".formatted(order.getId()))
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("should fail to change state with no body")
    @Order(6)
    void shouldFailToChangeStateWithNoBody() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .when()
          .put("/order/state/%s".formatted(order.getId()))
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("should fail to change state without bearer")
    @Order(7)
    void shouldFailToChangeStateWithoutBearer() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Content-Type", "application/json")
          .body("""
            {
              "state": "%s",
              "hash": "%s"
            }
            """.formatted(IN_DELIVERY, order.getHash()))
          .when()
          .put("/order/state/%s".formatted(order.getId()))
          .then()
          .statusCode(401);
    }

    @Test
    @DisplayName("should fail to change state without employee role")
    @Order(8)
    void shouldFailToChangeStateWithoutEmployeeRole() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "state": "%s",
              "hash": "%s"
            }
            """.formatted(DELIVERED, order.getHash()))
          .when()
          .put("/order/state/%s".formatted(order.getId()))
          .then()
          .statusCode(403);
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("sales_rep"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "state": "%s",
              "hash": "%s"
            }
            """.formatted(DELIVERED, order.getHash()))
          .when()
          .put("/order/state/%s".formatted(order.getId()))
          .then()
          .statusCode(403);
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "state": "%s",
              "hash": "%s"
            }
            """.formatted(DELIVERED, order.getHash()))
          .when()
          .put("/order/state/%s".formatted(order.getId()))
          .then()
          .statusCode(403);
    }
    @DisplayName("should fail to cancel order")
    @ParameterizedTest(name = "initialState, newState {0}, {1}")
    @CsvSource({
        "CREATED,CANCELLED",
        "COMPLETED,CANCELLED",
        "IN_DELIVERY,CANCELLED",
        "DELIVERED,CANCELLED",
    })
    @Order(9)
    void shouldFailToCancelOrder(OrderState initialState, OrderState newState) {
      OrderDto order = OrderUtil.createOrder(initialState);
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .header("Content-Type", "application/json")
          .body("""
            {
              "state": "%s",
              "hash": "%s"
            }
            """.formatted(newState, order.getHash()))
          .when()
          .put("/order/state/%s".formatted(order.getId()))
          .then()
          .statusCode(400)
          .body("message", equalTo("exception.moz.invalid.order.state.transition"));
    }
  }
}
