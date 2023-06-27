package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

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
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.OrderUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.28 - Show order details")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ28IT {
  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Positive {
    @Test
    @DisplayName("Should properly show order details")
    @Order(1)
    void shouldProperlyShowOrderDetails() {
      OrderDto order = OrderUtil.createOrder();
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .when()
          .get("/order/id/%s".formatted(order.getId()))
          .then()
          .statusCode(200)
          .body("id", notNullValue())
          .body("orderedProducts", notNullValue())
          .body("orderedProducts.amount", notNullValue())
          .body("orderedProducts.productId", notNullValue())
          .body("orderState", equalTo("CREATED"))
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
  }

  @Nested
  @Order(2)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Negative {
    @Test
    @DisplayName("Should fail if order does not exist")
    @Order(1)
    void shouldFailIfOrderDoesNotExist() {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
          .when()
          .get("/order/id/%s".formatted(99999))
          .then()
          .statusCode(404)
          .body("message", equalTo("exception.moz.order.not.found"));
    }

    @DisplayName("Should fail if user is not employee")
    @ParameterizedTest(name = "invalidLogin {0}")
    @CsvSource({
        "client",
        "administrator",
        "sales_rep",
    })
    @Order(2)
    void shouldFailIfUserIsNotEmployee(String invalidLogin) {
      given()
          .header("Authorization", "Bearer " + AuthUtil.retrieveToken(invalidLogin))
          .when()
          .get("/order/id/%s".formatted(999))
          .then()
          .statusCode(403);
    }

    @Test
    @DisplayName("Should fail if bearer is missing")
    @Order(3)
    void shouldFailIfBearerIsMissing() {
      given()
          .when()
          .get("/order/id/%s".formatted(999))
          .then()
          .statusCode(401);
    }
  }
}
