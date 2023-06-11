package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.CANCELLED;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.COMPLETED;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.IN_DELIVERY;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.8 - Display orders")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ8IT {
  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class findAll {
    @Test
    @DisplayName("Should properly return orders")
    @Order(1)
    void shouldProperlyReturnOrders() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .contentType("application/json")
          .when()
          .get("/order")
          .then()
          .statusCode(200)
          .body("size()", equalTo(2))
          .rootPath("find { it.id == 1 }")
            .body("id", equalTo(1))
            .body("hash", notNullValue())
            .body("observed", equalTo(false))
            .body("accountId", notNullValue())
            .body("accountLogin", equalTo("client"))
            .body("recipientAddress.city", equalTo("Lodz"))
            .body("recipientAddress.country", equalTo("Poland"))
            .body("recipientAddress.postalCode", equalTo("93-116"))
            .body("recipientAddress.street", equalTo("Przybyszewskiego"))
            .body("recipientAddress.streetNumber", equalTo(13))
            .body("orderedProducts", notNullValue())
            .body("orderedProducts[0].amount", equalTo(4))
            .body("orderedProducts[0].productId", equalTo(111));
    }

    @Test
    @DisplayName("Should fail if access token was not given")
    @Order(2)
    void shouldFailIfAccessTokenWasNotGiven() {
      given()
          .contentType("application/json")
          .when()
          .get("/order")
          .then()
          .statusCode(401);
    }

    @Test
    @DisplayName("Should fail if user is not employee")
    @Order(3)
    void shouldFailIfUserIsNotEmployee() {
      List<String> tokenList = new ArrayList<>();
      tokenList.add(InitData.retrieveAdminToken());
      tokenList.add(InitData.retrieveSalesRepToken());
      tokenList.add(InitData.retrieveClientToken());

      for (String token : tokenList) {
        given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .when()
            .get("/order")
            .then()
            .statusCode(403);
      }
    }
  }

  @Nested
  @Order(2)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class findAllPresent {
    @Test
    @DisplayName("Should properly return present orders")
    @Order(1)
    void shouldProperlyReturnOrders() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .contentType("application/json")
          .when()
          .get("/order/present")
          .then()
          .statusCode(200)
          .body("size()", equalTo(1))
          .rootPath("find { it.id == 1 }")
          .body("id", equalTo(1))
          .body("hash", notNullValue())
          .body("observed", equalTo(false))
          .body("accountId", notNullValue())
          .body("accountLogin", equalTo("client"))
          .body("recipientAddress.city", equalTo("Lodz"))
          .body("recipientAddress.country", equalTo("Poland"))
          .body("recipientAddress.postalCode", equalTo("93-116"))
          .body("recipientAddress.street", equalTo("Przybyszewskiego"))
          .body("recipientAddress.streetNumber", equalTo(13))
          .body("orderedProducts", notNullValue())
          .body("orderedProducts[0].amount", equalTo(4))
          .body("orderedProducts[0].productId", equalTo(111));
    }

    @Test
    @DisplayName("Should fail if access token was not given")
    @Order(2)
    void shouldFailIfAccessTokenWasNotGiven() {
      given()
          .contentType("application/json")
          .when()
          .get("/order/present")
          .then()
          .statusCode(401);
    }

    @Test
    @DisplayName("Should fail if user is not employee")
    @Order(3)
    void shouldFailIfUserIsNotEmployee() {
      List<String> tokenList = new ArrayList<>();
      tokenList.add(InitData.retrieveAdminToken());
      tokenList.add(InitData.retrieveSalesRepToken());
      tokenList.add(InitData.retrieveClientToken());

      for (String token : tokenList) {
        given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .when()
            .get("/order/present")
            .then()
            .statusCode(403);
      }
    }
  }

  @Nested
  @Order(3)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class findAllArchive {
    @Test
    @DisplayName("Should properly return archive order")
    @Order(1)
    void shouldProperlyReturnOrders() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .contentType("application/json")
          .when()
          .get("/order/archived")
          .then()
          .statusCode(200)
          .body("size()", equalTo(1))
          .rootPath("find { it.id == 2 }")
          .body("id", equalTo(2))
          .body("hash", notNullValue())
          .body("observed", equalTo(false))
          .body("accountId", notNullValue())
          .body("accountLogin", equalTo("client"))
          .body("recipientAddress.city", equalTo("Lodz"))
          .body("recipientAddress.country", equalTo("Poland"))
          .body("recipientAddress.postalCode", equalTo("93-590"))
          .body("recipientAddress.street", equalTo("Politechniki"))
          .body("recipientAddress.streetNumber", equalTo(50))
          .body("orderedProducts", notNullValue())
          .body("orderedProducts[0].amount", equalTo(2))
          .body("orderedProducts[0].productId", equalTo(112));
    }

    @Test
    @DisplayName("Should fail if access token was not given")
    @Order(2)
    void shouldFailIfAccessTokenWasNotGiven() {
      given()
          .contentType("application/json")
          .when()
          .get("/order/archived")
          .then()
          .statusCode(401);
    }

    @Test
    @DisplayName("Should fail if user is not employee")
    @Order(3)
    void shouldFailIfUserIsNotEmployee() {
      List<String> tokenList = new ArrayList<>();
      tokenList.add(InitData.retrieveAdminToken());
      tokenList.add(InitData.retrieveSalesRepToken());
      tokenList.add(InitData.retrieveClientToken());

      for (String token : tokenList) {
        given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .when()
            .get("/order/archived")
            .then()
            .statusCode(403);
      }
    }
  }

  @Nested
  @Order(4)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class findByState {
    @Test
    @DisplayName("should properly return orders by state")
    @Order(1)
    void shouldProperlyReturnOrdersByState() {
      List<OrderState> nonExistingOrderStates = List.of(COMPLETED, IN_DELIVERY, CANCELLED);
      for (OrderState orderState : nonExistingOrderStates) {
        given()
            .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
            .contentType("application/json")
            .when()
            .get("/order/state/" + orderState)
            .then()
            .statusCode(200)
            .body("size()", equalTo(0));
      }

      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .contentType("application/json")
          .when()
          .get("/order/state/CREATED")
          .then()
          .statusCode(200)
          .body("size()", equalTo(1))
          .rootPath("find { it.id == 1 }")
          .body("id", equalTo(1))
          .body("hash", notNullValue())
          .body("observed", equalTo(false))
          .body("accountId", notNullValue())
          .body("accountLogin", equalTo("client"))
          .body("recipientAddress.city", equalTo("Lodz"))
          .body("recipientAddress.country", equalTo("Poland"))
          .body("recipientAddress.postalCode", equalTo("93-116"))
          .body("recipientAddress.street", equalTo("Przybyszewskiego"))
          .body("recipientAddress.streetNumber", equalTo(13))
          .body("orderedProducts", notNullValue())
          .body("orderedProducts[0].amount", equalTo(4))
          .body("orderedProducts[0].productId", equalTo(111));

      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .contentType("application/json")
          .when()
          .get("/order/state/DELIVERED")
          .then()
          .statusCode(200)
          .body("size()", equalTo(1))
          .rootPath("find { it.id == 2 }")
          .body("id", equalTo(2))
          .body("hash", notNullValue())
          .body("observed", equalTo(false))
          .body("accountId", notNullValue())
          .body("accountLogin", equalTo("client"))
          .body("recipientAddress.city", equalTo("Lodz"))
          .body("recipientAddress.country", equalTo("Poland"))
          .body("recipientAddress.postalCode", equalTo("93-590"))
          .body("recipientAddress.street", equalTo("Politechniki"))
          .body("recipientAddress.streetNumber", equalTo(50))
          .body("orderedProducts", notNullValue())
          .body("orderedProducts[0].amount", equalTo(2))
          .body("orderedProducts[0].productId", equalTo(112));
    }

    @Test
    @DisplayName("Should fail if access token was not given")
    @Order(2)
    void shouldFailIfAccessTokenWasNotGiven() {
      given()
          .contentType("application/json")
          .when()
          .get("/order/state/COMPLETED")
          .then()
          .statusCode(401);
    }

    @Test
    @DisplayName("Should fail if user is not employee")
    @Order(3)
    void shouldFailIfUserIsNotEmployee() {
      List<String> tokenList = new ArrayList<>();
      tokenList.add(InitData.retrieveAdminToken());
      tokenList.add(InitData.retrieveSalesRepToken());
      tokenList.add(InitData.retrieveClientToken());

      for (String token : tokenList) {
        given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .when()
            .get("/order/state/CREATED")
            .then()
            .statusCode(403);
      }
    }
  }
}
