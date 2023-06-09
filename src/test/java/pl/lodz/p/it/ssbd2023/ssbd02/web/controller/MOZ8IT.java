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
            .body("account", notNullValue())
            .body("recipientAddress.city", equalTo("Lodz"))
            .body("recipientAddress.country", equalTo("Poland"))
            .body("recipientAddress.postalCode", equalTo("93-116"))
            .body("recipientAddress.street", equalTo("Przybyszewskiego"))
            .body("recipientAddress.streetNumber", equalTo(13))
            .body("orderProductList", notNullValue())
            .body("orderProductList[0].amount", equalTo(4))
            .body("orderProductList[0].price", equalTo(3299.99f))
            .body("orderProductList[0].product.amount", equalTo(0))
            .body("orderProductList[0].product.archive", equalTo(true))
            .body("orderProductList[0].product.color", equalTo("BLACK"))
            .body("orderProductList[0].product.furnitureDimensions.depth", equalTo(190))
            .body("orderProductList[0].product.furnitureDimensions.height", equalTo(36))
            .body("orderProductList[0].product.furnitureDimensions.width", equalTo(137))
            .body("orderProductList[0].product.id", equalTo(111))
            .body("orderProductList[0].product.imageUrl", equalTo("https://storage.googleapis.com/furniture-store-images/111.jpg"))
            .body("orderProductList[0].product.packageDimensions.depth", equalTo(195))
            .body("orderProductList[0].product.packageDimensions.height", equalTo(40))
            .body("orderProductList[0].product.packageDimensions.width", equalTo(141))
            .body("orderProductList[0].product.price", equalTo(3299.99f))
            .body("orderProductList[0].product.productGroup", notNullValue())
            .body("orderProductList[0].product.productGroup.archive", equalTo(false))
            .body("orderProductList[0].product.productGroup.averageRating", equalTo(0f))
            .body("orderProductList[0].product.productGroup.id", equalTo(50)) .body("orderProductList.product[0].productGroup.name", equalTo("HarmonyHaven Double Bed"))
            .body("orderProductList[0].product.weight", equalTo(40.3f))
            .body("orderProductList[0].product.weightInPackage", equalTo(44.1f))
            .body("orderProductList[0].product.woodType", equalTo("DARK_OAK"));
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
          .body("account", notNullValue())
          .body("recipientAddress.city", equalTo("Lodz"))
          .body("recipientAddress.country", equalTo("Poland"))
          .body("recipientAddress.postalCode", equalTo("93-116"))
          .body("recipientAddress.street", equalTo("Przybyszewskiego"))
          .body("recipientAddress.streetNumber", equalTo(13))
          .body("orderProductList", notNullValue())
          .body("orderProductList[0].amount", equalTo(4))
          .body("orderProductList[0].price", equalTo(3299.99f))
          .body("orderProductList[0].product.amount", equalTo(0))
          .body("orderProductList[0].product.archive", equalTo(true))
          .body("orderProductList[0].product.color", equalTo("BLACK"))
          .body("orderProductList[0].product.furnitureDimensions.depth", equalTo(190))
          .body("orderProductList[0].product.furnitureDimensions.height", equalTo(36))
          .body("orderProductList[0].product.furnitureDimensions.width", equalTo(137))
          .body("orderProductList[0].product.id", equalTo(111))
          .body("orderProductList[0].product.imageUrl", equalTo("https://storage.googleapis.com/furniture-store-images/111.jpg"))
          .body("orderProductList[0].product.packageDimensions.depth", equalTo(195))
          .body("orderProductList[0].product.packageDimensions.height", equalTo(40))
          .body("orderProductList[0].product.packageDimensions.width", equalTo(141))
          .body("orderProductList[0].product.price", equalTo(3299.99f))
          .body("orderProductList[0].product.productGroup", notNullValue())
          .body("orderProductList[0].product.productGroup.archive", equalTo(false))
          .body("orderProductList[0].product.productGroup.averageRating", equalTo(0f))
          .body("orderProductList[0].product.productGroup.id", equalTo(50)) .body("orderProductList.product[0].productGroup.name", equalTo("HarmonyHaven Double Bed"))
          .body("orderProductList[0].product.weight", equalTo(40.3f))
          .body("orderProductList[0].product.weightInPackage", equalTo(44.1f))
          .body("orderProductList[0].product.woodType", equalTo("DARK_OAK"));
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
          .body("account", notNullValue())
          .body("recipientAddress.city", equalTo("Lodz"))
          .body("recipientAddress.country", equalTo("Poland"))
          .body("recipientAddress.postalCode", equalTo("93-590"))
          .body("recipientAddress.street", equalTo("Politechniki"))
          .body("recipientAddress.streetNumber", equalTo(50))
          .body("orderProductList", notNullValue())
          .body("orderProductList[0].amount", equalTo(2))
          .body("orderProductList[0].price", equalTo(3299.99f))
          .body("orderProductList[0].product.amount", equalTo(0))
          .body("orderProductList[0].product.archive", equalTo(true))
          .body("orderProductList[0].product.color", equalTo("RED"))
          .body("orderProductList[0].product.furnitureDimensions.depth", equalTo(190))
          .body("orderProductList[0].product.furnitureDimensions.height", equalTo(36))
          .body("orderProductList[0].product.furnitureDimensions.width", equalTo(137))
          .body("orderProductList[0].product.id", equalTo(112))
          .body("orderProductList[0].product.imageUrl", equalTo("https://storage.googleapis.com/furniture-store-images/112.jpg"))
          .body("orderProductList[0].product.packageDimensions.depth", equalTo(195))
          .body("orderProductList[0].product.packageDimensions.height", equalTo(40))
          .body("orderProductList[0].product.packageDimensions.width", equalTo(141))
          .body("orderProductList[0].product.price", equalTo(3299.99f))
          .body("orderProductList[0].product.productGroup", notNullValue())
          .body("orderProductList[0].product.productGroup.archive", equalTo(false))
          .body("orderProductList[0].product.productGroup.averageRating", equalTo(0f))
          .body("orderProductList[0].product.productGroup.id", equalTo(50)) .body("orderProductList.product[0].productGroup.name", equalTo("HarmonyHaven Double Bed"))
          .body("orderProductList[0].product.weight", equalTo(40.3f))
          .body("orderProductList[0].product.weightInPackage", equalTo(44.1f))
          .body("orderProductList[0].product.woodType", equalTo("DARK_OAK"));
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
          .body("account", notNullValue())
          .body("recipientAddress.city", equalTo("Lodz"))
          .body("recipientAddress.country", equalTo("Poland"))
          .body("recipientAddress.postalCode", equalTo("93-116"))
          .body("recipientAddress.street", equalTo("Przybyszewskiego"))
          .body("recipientAddress.streetNumber", equalTo(13))
          .body("orderProductList", notNullValue())
          .body("orderProductList[0].amount", equalTo(4))
          .body("orderProductList[0].price", equalTo(3299.99f))
          .body("orderProductList[0].product.amount", equalTo(0))
          .body("orderProductList[0].product.archive", equalTo(true))
          .body("orderProductList[0].product.color", equalTo("BLACK"))
          .body("orderProductList[0].product.furnitureDimensions.depth", equalTo(190))
          .body("orderProductList[0].product.furnitureDimensions.height", equalTo(36))
          .body("orderProductList[0].product.furnitureDimensions.width", equalTo(137))
          .body("orderProductList[0].product.id", equalTo(111))
          .body("orderProductList[0].product.imageUrl", equalTo("https://storage.googleapis.com/furniture-store-images/111.jpg"))
          .body("orderProductList[0].product.packageDimensions.depth", equalTo(195))
          .body("orderProductList[0].product.packageDimensions.height", equalTo(40))
          .body("orderProductList[0].product.packageDimensions.width", equalTo(141))
          .body("orderProductList[0].product.price", equalTo(3299.99f))
          .body("orderProductList[0].product.productGroup", notNullValue())
          .body("orderProductList[0].product.productGroup.archive", equalTo(false))
          .body("orderProductList[0].product.productGroup.averageRating", equalTo(0f))
          .body("orderProductList[0].product.productGroup.id", equalTo(50)) .body("orderProductList.product[0].productGroup.name", equalTo("HarmonyHaven Double Bed"))
          .body("orderProductList[0].product.weight", equalTo(40.3f))
          .body("orderProductList[0].product.weightInPackage", equalTo(44.1f))
          .body("orderProductList[0].product.woodType", equalTo("DARK_OAK"));

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
          .body("account", notNullValue())
          .body("recipientAddress.city", equalTo("Lodz"))
          .body("recipientAddress.country", equalTo("Poland"))
          .body("recipientAddress.postalCode", equalTo("93-590"))
          .body("recipientAddress.street", equalTo("Politechniki"))
          .body("recipientAddress.streetNumber", equalTo(50))
          .body("orderProductList", notNullValue())
          .body("orderProductList[0].amount", equalTo(2))
          .body("orderProductList[0].price", equalTo(3299.99f))
          .body("orderProductList[0].product.amount", equalTo(0))
          .body("orderProductList[0].product.archive", equalTo(true))
          .body("orderProductList[0].product.color", equalTo("RED"))
          .body("orderProductList[0].product.furnitureDimensions.depth", equalTo(190))
          .body("orderProductList[0].product.furnitureDimensions.height", equalTo(36))
          .body("orderProductList[0].product.furnitureDimensions.width", equalTo(137))
          .body("orderProductList[0].product.id", equalTo(112))
          .body("orderProductList[0].product.imageUrl", equalTo("https://storage.googleapis.com/furniture-store-images/112.jpg"))
          .body("orderProductList[0].product.packageDimensions.depth", equalTo(195))
          .body("orderProductList[0].product.packageDimensions.height", equalTo(40))
          .body("orderProductList[0].product.packageDimensions.width", equalTo(141))
          .body("orderProductList[0].product.price", equalTo(3299.99f))
          .body("orderProductList[0].product.productGroup", notNullValue())
          .body("orderProductList[0].product.productGroup.archive", equalTo(false))
          .body("orderProductList[0].product.productGroup.averageRating", equalTo(0f))
          .body("orderProductList[0].product.productGroup.id", equalTo(50)) .body("orderProductList.product[0].productGroup.name", equalTo("HarmonyHaven Double Bed"))
          .body("orderProductList[0].product.weight", equalTo(40.3f))
          .body("orderProductList[0].product.weightInPackage", equalTo(44.1f))
          .body("orderProductList[0].product.woodType", equalTo("DARK_OAK"));

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
