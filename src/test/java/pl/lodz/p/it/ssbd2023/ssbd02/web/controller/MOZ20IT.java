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
@DisplayName("MOZ.20 - Remove rate")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MOZ20IT {

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

  @Order(6)
  @DisplayName("Should not remove rate without token")
  @Test
  void shouldNotRemoveRateWithoutToken() {
    given()
            .header("Content-Type", "application/json")
            .when()
            .delete("/rate/id/" + products.get(1).getProduct().getProductGroup().getId())
            .then()
            .statusCode(401);
  }

  @Order(7)
  @DisplayName("Should not add rate with token with different role")
  @Test
  void shouldNotRemoveRateWithTokenWithDifferentRole() {
    given()
            .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
            .header("Content-Type", "application/json")
            .when()
            .delete("/rate/id/" + products.get(1).getProduct().getProductGroup().getId())
            .then()
            .statusCode(403);
  }

}
