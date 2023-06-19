package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.lodz.p.it.ssbd2023.ssbd02.web.InitData.getAllClientProducts;

import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderProductWithRateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.18 - Change rate")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MOZ18IT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }

  private final List<OrderProductWithRateDto> products = getAllClientProducts();


  @Order(1)
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
            """.formatted(products.get(products.size() - 1).getProduct().getProductGroup().getId()))
            .when()
            .post("/rate")
            .then()
            .statusCode(200)
            .body("rate", equalTo(3));
  }

  @Order(2)
  @DisplayName("Should set new avg")
  @Test
  void shouldSetNewAvg() {
    assertTrue(products.size() > 0);
    var dto = products.get(products.size() - 1);
    assertEquals(3, dto.getRate());
    assertEquals(3.0, dto.getProduct().getProductGroup().getAverageRating());
  }

  @Order(3)
  @DisplayName("Should not change existing not valid rate")
  @Test
  void shouldNotChangeExistingNotValidRate() {
    given()
            .header("Authorization", "Bearer " + InitData.retrieveClientToken())
            .header("Content-Type", "application/json")
            .body("""
            {
              "rate": 0,
              "productId":  %d
            }
            """.formatted(products.get(products.size() - 1).getProduct().getProductGroup().getId()))
            .when()
            .post("/rate")
            .then()
            .statusCode(400);
  }

  @Order(4)
  @DisplayName("Should change existing rate")
  @Test
  void shouldChangeExistingRate() {
    given()
            .header("Authorization", "Bearer " + InitData.retrieveClientToken())
            .header("Content-Type", "application/json")
            .body("""
            {
              "rate": 2,
              "productId":  %d
            }
            """.formatted(products.get(products.size() - 1).getProduct().getProductGroup().getId()))
            .when()
            .put("/rate")
            .then()
            .statusCode(200);
  }

  @Order(5)
  @DisplayName("Should set new avg after change")
  @Test
  void shouldSetNewAvgAfterChange() {
    assertTrue(products.size() > 0);
    var dto = products.get(products.size() - 1);
    assertEquals(2, dto.getRate());
    assertEquals(2.0, dto.getProduct().getProductGroup().getAverageRating());
  }

}
