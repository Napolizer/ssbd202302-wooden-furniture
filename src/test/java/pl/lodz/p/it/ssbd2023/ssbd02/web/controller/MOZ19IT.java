package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.*;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderProductWithRateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.lodz.p.it.ssbd2023.ssbd02.web.InitData.getAllClientProducts;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.19 - Add rate")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ19IT {

  private final List<OrderProductWithRateDto> products = getAllClientProducts();
  private static int counter = 0;

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {
    @Order(1)
    @DisplayName("Should list size be at least size 1 with 0 rate")
    @Test
    void shouldListHasAtLeastOneElementWith0Rate() {
      assertTrue(products.size() > 0);
      var dto = products.get(0);
      assertEquals(0, dto.getRate());
      assertEquals(0.0, dto.getProduct().getProductGroup().getAverageRating());
    }

    @Order(2)
    @DisplayName("Should add valid rate")
    @Test
    void shouldAddValidRate() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveClientToken())
              .header("Content-Type", "application/json")
              .body("""
            {
              "rate": 3,
              "productId":  %d
            }
            """.formatted(products.get(0).getProduct().getProductGroup().getId()))
              .when()
              .post("/rate")
              .then()
              .statusCode(200)
              .body("rate", equalTo(3));
    }

    @Order(3)
    @DisplayName("Should set new avg")
    @Test
    void shouldSetNewAvg() {
      assertTrue(products.size() > 0);
      var dto = products.get(0);
      assertEquals(3, dto.getRate());
      assertEquals(3.0, dto.getProduct().getProductGroup().getAverageRating());
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {
    @Order(1)
    @DisplayName("Should not add rate smaller than 1")
    @Test
    void shouldNotAddTooSmallRate() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveClientToken())
              .header("Content-Type", "application/json")
              .body("""
            {
              "rate": 0,
              "productId":  %d
            }
            """.formatted(products.get(0).getProduct().getProductGroup().getId()))
              .when()
              .post("/rate")
              .then()
              .statusCode(400);
    }

    @Order(2)
    @DisplayName("Should not update avg after too small value")
    @Test
    void shouldNotUpdateAvgAfterTooSmallValue() {
      assertTrue(products.size() > 0);
      var dto = products.get(0);
      assertEquals(3, dto.getRate());
      assertEquals(3.0, dto.getProduct().getProductGroup().getAverageRating());
    }

    @Order(3)
    @DisplayName("Should not add rate bigger than 5")
    @Test
    void shouldNotAddTooBigRate() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveClientToken())
              .header("Content-Type", "application/json")
              .body("""
            {
              "rate": 6,
              "productId":  %d
            }
            """.formatted(products.get(0).getProduct().getProductGroup().getId()))
              .when()
              .post("/rate")
              .then()
              .statusCode(400);
    }

    @Order(4)
    @DisplayName("Should not update avg after too big value")
    @Test
    void shouldNotSetNewAvgAfterTooBigValue() {
      assertTrue(products.size() > 0);
      var dto = products.get(0);
      assertEquals(3, dto.getRate());
      assertEquals(3.0, dto.getProduct().getProductGroup().getAverageRating());
    }

    @Order(5)
    @DisplayName("Should not add rate when product group doesn't exist")
    @Test
    void shouldNotAddWhenProductGroupDoesNotExist() {
      given()
              .header("Authorization", "Bearer " + InitData.retrieveClientToken())
              .header("Content-Type", "application/json")
              .body("""
            {
              "rate": 3,
              "productId":  100000
            }
            """)
              .when()
              .post("/rate")
              .then()
              .statusCode(404);
    }
  }

}
