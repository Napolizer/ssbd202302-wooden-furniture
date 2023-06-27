package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
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
public class MOZ29IT {

  @Test
  @DisplayName("Should properly show order details")
  @Order(1)
  void shouldProperlyShowOrderDetails() {
    OrderDto order = OrderUtil.createOrder();
    given()
            .header("Authorization", "Bearer " + AuthUtil.retrieveToken("client"))
            .when()
            .get("/order/id/%s/client".formatted(order.getId()))
            .then()
            .statusCode(200);
  }

  @Test
  @DisplayName("Should fail if user is not client")
  @Order(2)
  void shouldFailIfUserIsNotClient() {
    OrderDto order = OrderUtil.createOrder();
    given()
            .header("Authorization", "Bearer " + AuthUtil.retrieveToken("employee"))
            .when()
            .get("/order/id/%s/client".formatted(order.getId()))
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
