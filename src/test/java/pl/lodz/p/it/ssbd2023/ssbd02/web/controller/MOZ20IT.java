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
@DisplayName("MOZ.20 - Remove rate")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MOZ20IT {
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
        .when()
        .delete("/rate/id/" + products.get(1).getProduct().getProductGroup().getId());

    given()
            .header("Authorization", "Bearer " + InitData.retrieveClientToken())
            .header("Content-Type", "application/json")
            .body("""
            {
              "rate": 3,
              "productId":  %d
            }
            """.formatted(products.get(1).getProduct().getProductGroup().getId()))
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
    var dto = products.get(1);
    assertEquals(3, dto.getRate());
    assertEquals(3.5, dto.getProduct().getProductGroup().getAverageRating());
  }

  @Order(3)
  @DisplayName("Should not remove not existing rate")
  @Test
  void shouldNotRemoveNotExistingRate() {
    given()
            .header("Authorization", "Bearer " + InitData.retrieveClientToken())
            .when()
            .delete("/rate/id/33")
            .then()
            .statusCode(404);
  }

  @Order(4)
  @DisplayName("Should remove rate")
  @Test
  void shouldRemoveRate() {
    given()
            .header("Authorization", "Bearer " + InitData.retrieveClientToken())
            .when()
            .delete("/rate/id/" + products.get(1).getProduct().getProductGroup().getId())
            .then()
            .statusCode(204);
  }

  @Order(5)
  @DisplayName("Should not remove removed rate")
  @Test
  void shouldNotRemoveRemovedRate() {
    given()
            .header("Authorization", "Bearer " + InitData.retrieveClientToken())
            .when()
            .delete("/rate/id/" + products.get(1).getProduct().getProductGroup().getId())
            .then()
            .statusCode(404);
  }
}
