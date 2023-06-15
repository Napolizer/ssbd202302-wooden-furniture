package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.14 - Show your orders")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MOZ14IT {

  @Test
  @DisplayName("Should properly client orders")
  @Order(1)
  void shouldProperlyReturnOrders() {
    given()
            .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
            .contentType("application/json")
            .when()
            .get("/customer")
            .then()
            .statusCode(200)
            .body("recipientAddress", notNullValue())
            .body("id", notNullValue())
            .body("orderedProducts", notNullValue())
            .body("observed", notNullValue())
            .body("totalPrice", notNullValue());
  }

  @Test
  @DisplayName("Should fail if access token was not given")
  @Order(2)
  void shouldFailIfAccessTokenWasNotGiven() {
    given()
            .contentType("application/json")
            .when()
            .get("/customer")
            .then()
            .statusCode(401);
  }

  @Test
  @DisplayName("Should fail if user is not client")
  @Order(3)
  void shouldFailIfUserIsNotClient() {
    List<String> tokenList = new ArrayList<>();
    tokenList.add(InitData.retrieveAdminToken());
    tokenList.add(InitData.retrieveSalesRepToken());
    tokenList.add(InitData.retrieveEmployeeToken());

    for (String token : tokenList) {
      given()
              .header("Authorization", "Bearer " + token)
              .contentType("application/json")
              .when()
              .get("/customer")
              .then()
              .statusCode(403);
    }
  }
}

