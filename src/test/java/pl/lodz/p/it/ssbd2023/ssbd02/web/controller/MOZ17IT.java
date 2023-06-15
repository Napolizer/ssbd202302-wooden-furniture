package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AddressDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.ShippingDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderedProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateWithImageDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.ProductUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.17 - Make order")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MOZ17IT {
  public int productId1;
  public int productId2;
  public int productId3;

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {
    @Order(1)
    @DisplayName("Should properly create products")
    @Test
    void shouldProperlyCreateProducts() {
      ProductCreateWithImageDto productCreateWithImageDto1 = ProductCreateWithImageDto.builder()
          .price(200.0)
          .amount(2)
          .weight(55.5)
          .weightInPackage(60.5)
          .furnitureWidth(300)
          .furnitureHeight(70)
          .furnitureDepth(40)
          .packageWidth(310)
          .packageHeight(80)
          .packageDepth(40)
          .color(Color.BROWN.name())
          .woodType(WoodType.BIRCH.name())
          .productGroupId(1L)
          .imageProductId(1L)
          .build();

      ProductCreateWithImageDto productCreateWithImageDto2 = ProductCreateWithImageDto.builder()
          .price(300.0)
          .amount(2)
          .weight(55.5)
          .weightInPackage(60.5)
          .furnitureWidth(300)
          .furnitureHeight(70)
          .furnitureDepth(40)
          .packageWidth(310)
          .packageHeight(80)
          .packageDepth(40)
          .color(Color.BLACK.name())
          .woodType(WoodType.BIRCH.name())
          .productGroupId(1L)
          .imageProductId(2L)
          .build();

      ProductCreateWithImageDto productCreateWithImageDto3 = ProductCreateWithImageDto.builder()
          .price(400.0)
          .amount(2)
          .weight(55.5)
          .weightInPackage(60.5)
          .furnitureWidth(300)
          .furnitureHeight(70)
          .furnitureDepth(40)
          .packageWidth(310)
          .packageHeight(80)
          .packageDepth(40)
          .color(Color.BROWN.name())
          .woodType(WoodType.OAK.name())
          .productGroupId(2L)
          .imageProductId(4L)
          .build();

      productId1 = ProductUtil.createProductWithExistingImage(productCreateWithImageDto1);
      productId2 = ProductUtil.createProductWithExistingImage(productCreateWithImageDto2);
      productId3 = ProductUtil.createProductWithExistingImage(productCreateWithImageDto3);
    }

    @Order(2)
    @DisplayName("Should properly create account to make order")
    @Test
    void shouldProperlyCreateAccountToMakeOrder() {
      int id = AccountUtil.registerUser("makesOrder");
      given()
          .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
          .when()
          .patch("/account/activate/" + id)
          .then()
          .statusCode(200);
    }

    @Order(3)
    @DisplayName("Should properly create order without shipping data")
    @Test
    void shouldProperlyCreateOrderWithoutShippingData() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId1)
          .amount(1)
          .build());
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId2)
          .amount(1)
          .build());
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId3)
          .amount(1)
          .build());

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .build();

      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");

      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .body(InitData.mapToJsonString(createOrderDto))
          .when()
          .post("/order/create")
          .then()
          .statusCode(201)
          .body("id", is(notNullValue()))
          .body("orderedProducts.size()", is(equalTo(3)))
          .body("orderedProducts[0].amount", is(equalTo(1)))
          .body("orderedProducts[0].productId", is(equalTo(productId1)))
          .body("orderState", is(equalTo(OrderState.CREATED.toString())))
          .body("recipientFirstName", is(equalTo("John")))
          .body("recipientLastName", is(equalTo("Doe")))
          .body("recipientAddress.country", is(equalTo("Poland")))
          .body("recipientAddress.city", is(equalTo("Lodz")))
          .body("recipientAddress.street", is(equalTo("Piotrkowska")))
          .body("recipientAddress.streetNumber", is(equalTo(1)))
          .body("recipientAddress.postalCode", is(equalTo("90-000")))
          .body("accountLogin", is(equalTo("makesOrder")))
          .body("observed", is(equalTo(false)))
          .body("totalPrice", is(equalTo(900.0F)));
    }

    @Order(4)
    @DisplayName("Should properly create order with given shipping data")
    @Test
    void shouldProperlyCreateOrderWithGivenShippingData() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId1)
          .amount(1)
          .build());
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId2)
          .amount(1)
          .build());
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId3)
          .amount(1)
          .build());

      ShippingDataDto shippingDataDto = ShippingDataDto.builder()
          .recipientFirstName("Romelo")
          .recipientLastName("Lukaku")
          .recipientAddress(AddressDto.builder()
              .country("Italy")
              .city("Mediolan")
              .street("Street")
              .streetNumber(2)
              .postalCode("98-100")
              .build())
          .build();

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .shippingData(shippingDataDto)
          .build();

      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");

      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .body(InitData.mapToJsonString(createOrderDto))
          .when()
          .post("/order/create")
          .then()
          .statusCode(201)
          .body("id", is(notNullValue()))
          .body("orderedProducts.size()", is(equalTo(3)))
          .body("orderedProducts[0].amount", is(equalTo(1)))
          .body("orderedProducts[0].productId", is(equalTo(productId1)))
          .body("orderState", is(equalTo(OrderState.CREATED.toString())))
          .body("recipientFirstName", is(equalTo("Romelo")))
          .body("recipientLastName", is(equalTo("Lukaku")))
          .body("recipientAddress.country", is(equalTo("Italy")))
          .body("recipientAddress.city", is(equalTo("Mediolan")))
          .body("recipientAddress.street", is(equalTo("Street")))
          .body("recipientAddress.streetNumber", is(equalTo(2)))
          .body("recipientAddress.postalCode", is(equalTo("98-100")))
          .body("accountLogin", is(equalTo("makesOrder")))
          .body("observed", is(equalTo(false)))
          .body("totalPrice", is(equalTo(900.0F)));
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {

    @Order(1)
    @DisplayName("Should fail to create order without token")
    @Test
    void shouldFailToCreateOrderWithoutToken() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId1)
          .amount(1)
          .build());

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .build();

      given()
          .header(CONTENT_TYPE, "application/json")
          .body(InitData.mapToJsonString(createOrderDto))
          .when()
          .post("/order/create")
          .then()
          .statusCode(401);
    }

    @Order(2)
    @DisplayName("Should fail with invalid access level")
    @Test
    void shouldFailWithInvalidAccessLevel() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId1)
          .amount(1)
          .build());

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .build();

      List<String> tokenList = new ArrayList<>();
      tokenList.add(InitData.retrieveAdminToken());
      tokenList.add(InitData.retrieveSalesRepToken());
      tokenList.add(InitData.retrieveEmployeeToken());

      for (String token : tokenList) {
        given()
            .header(AUTHORIZATION, "Bearer " + token)
            .header(CONTENT_TYPE, "application/json")
            .body(InitData.mapToJsonString(createOrderDto))
            .when()
            .post("/order/create")
            .then()
            .statusCode(403);
      }
    }

    @Order(3)
    @DisplayName("Should fail to create order without body")
    @Test
    void shouldFailToCreateOrderWithoutBody() {
      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");
      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .when()
          .post("/order/create")
          .then()
          .statusCode(400);
    }

    @Order(4)
    @DisplayName("Should fail to create order with empty body")
    @Test
    void shouldFailToCreateOrderWithEmptyBody() {
      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");
      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .body("")
          .when()
          .post("/order/create")
          .then()
          .statusCode(400);
    }

    @Order(5)
    @DisplayName("Should fail to create order with empty products")
    @Test
    void shouldFailToCreateOrderWithEmptyProducts() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .build();

      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");

      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .body(InitData.mapToJsonString(createOrderDto))
          .when()
          .post("/order/create")
          .then()
          .statusCode(400);
    }

    @Order(6)
    @DisplayName("Should fail to create order with invalid amount in ordered product")
    @Test
    void shouldFailToCreateOrderWithInvalidAmountInProduct() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId1)
          .amount(-1)
          .build());

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .build();

      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");

      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .body(InitData.mapToJsonString(createOrderDto))
          .when()
          .post("/order/create")
          .then()
          .statusCode(400);
    }

    @Order(7)
    @DisplayName("Should fail to create order with invalid productId in ordered product")
    @Test
    void shouldFailToCreateOrderWithInvalidProductIdInOrderedProduct() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();
      orderedProducts.add(OrderedProductDto.builder()
          .productId(-1L)
          .amount(1)
          .build());

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .build();

      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");

      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .body(InitData.mapToJsonString(createOrderDto))
          .when()
          .post("/order/create")
          .then()
          .statusCode(400);
    }

    @Order(8)
    @DisplayName("Should fail to create order with non existing productId in ordered product")
    @Test
    void shouldFailToCreateOrderWithNonExistingProductIdInOrderedProduct() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();
      orderedProducts.add(OrderedProductDto.builder()
          .productId(Long.MAX_VALUE)
          .amount(1)
          .build());

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .build();

      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");

      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .body(InitData.mapToJsonString(createOrderDto))
          .when()
          .post("/order/create")
          .then()
          .statusCode(404);
    }

    @Order(9)
    @DisplayName("Should fail to create order with invalid recipient first name")
    @Test
    void shouldFailToCreateOrderWithInvalidRecipientFirstName() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId1)
          .amount(1)
          .build());

      ShippingDataDto shippingDataDto = ShippingDataDto.builder()
          .recipientFirstName("invalid")
          .recipientLastName("Lukaku")
          .recipientAddress(AddressDto.builder()
              .country("Italy")
              .city("Mediolan")
              .street("Street")
              .streetNumber(2)
              .postalCode("98-100")
              .build())
          .build();

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .shippingData(shippingDataDto)
          .build();

      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");

      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .body(InitData.mapToJsonString(createOrderDto))
          .when()
          .post("/order/create")
          .then()
          .statusCode(400);
    }

    @Order(10)
    @DisplayName("Should fail to create order with invalid recipient last name")
    @Test
    void shouldFailToCreateOrderWithInvalidRecipientLastName() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId1)
          .amount(1)
          .build());

      ShippingDataDto shippingDataDto = ShippingDataDto.builder()
          .recipientFirstName("Romelo")
          .recipientLastName("invalid")
          .recipientAddress(AddressDto.builder()
              .country("Italy")
              .city("Mediolan")
              .street("Street")
              .streetNumber(2)
              .postalCode("98-100")
              .build())
          .build();

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .shippingData(shippingDataDto)
          .build();

      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");

      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .body(InitData.mapToJsonString(createOrderDto))
          .when()
          .post("/order/create")
          .then()
          .statusCode(400);
    }

    @Order(11)
    @DisplayName("Should fail to create order with invalid country in recipient address")
    @Test
    void shouldFailToCreateOrderWithInvalidCountryInRecipientAddress() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId1)
          .amount(1)
          .build());

      ShippingDataDto shippingDataDto = ShippingDataDto.builder()
          .recipientFirstName("Romelo")
          .recipientLastName("Lukaku")
          .recipientAddress(AddressDto.builder()
              .country("invalid")
              .city("Mediolan")
              .street("Street")
              .streetNumber(2)
              .postalCode("98-100")
              .build())
          .build();

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .shippingData(shippingDataDto)
          .build();

      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");

      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .body(InitData.mapToJsonString(createOrderDto))
          .when()
          .post("/order/create")
          .then()
          .statusCode(400);
    }

    @Order(12)
    @DisplayName("Should fail to create order with invalid street number in recipient address")
    @Test
    void shouldFailToCreateOrderWithInvalidStreetNumberInRecipientAddress() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId1)
          .amount(1)
          .build());

      ShippingDataDto shippingDataDto = ShippingDataDto.builder()
          .recipientFirstName("Romelo")
          .recipientLastName("Lukaku")
          .recipientAddress(AddressDto.builder()
              .country("Italy")
              .city("Mediolan")
              .street("Street")
              .streetNumber(-2)
              .postalCode("98-100")
              .build())
          .build();

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .shippingData(shippingDataDto)
          .build();

      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");

      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .body(InitData.mapToJsonString(createOrderDto))
          .when()
          .post("/order/create")
          .then()
          .statusCode(400);
    }

    @Order(13)
    @DisplayName("Should fail to create order with invalid postal code in recipient address")
    @Test
    void shouldFailToCreateOrderWithInvalidPostalCodeInRecipientAddress() {
      List<OrderedProductDto> orderedProducts = new ArrayList<>();
      orderedProducts.add(OrderedProductDto.builder()
          .productId((long) productId1)
          .amount(1)
          .build());

      ShippingDataDto shippingDataDto = ShippingDataDto.builder()
          .recipientFirstName("Romelo")
          .recipientLastName("Lukaku")
          .recipientAddress(AddressDto.builder()
              .country("Italy")
              .city("Mediolan")
              .street("Street")
              .streetNumber(2)
              .postalCode("invalid")
              .build())
          .build();

      CreateOrderDto createOrderDto = CreateOrderDto.builder()
          .products(orderedProducts)
          .shippingData(shippingDataDto)
          .build();

      String token = AuthUtil.retrieveToken("makesOrder", "Student123!");

      given()
          .header(AUTHORIZATION, "Bearer " + token)
          .header(CONTENT_TYPE, "application/json")
          .body(InitData.mapToJsonString(createOrderDto))
          .when()
          .post("/order/create")
          .then()
          .statusCode(400);
    }
  }
}
