package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.6 - Edit product group name")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ6IT {
  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Positive {
    @Test
    @DisplayName("Should properly edit product group name")
    @Order(1)
    void shouldProperlyEditProductGroupName() {
      ProductGroupCreateDto dto = new ProductGroupCreateDto("FirstName", "LOCKER");
      ProductGroupInfoDto productGroup = InitData.createProductGroup(dto);
      String token = InitData.retrieveEmployeeToken();
      String hash = productGroup.getHash();
      given()
          .header("Authorization", "Bearer " + token)
          .contentType("application/json")
          .body("""
            {
              "name": "Zmieniona",
              "hash": "$hash"
            }
          """.replace("$hash", hash))
          .when()
          .put("/product/group/id/" + productGroup.getId())
          .then()
          .statusCode(200)
          .body("name", equalTo("Zmieniona"));

      hash = given()
          .header("Authorization", "Bearer " + token)
          .when()
          .get("/product/group/id/" + productGroup.getId())
          .then()
          .contentType(MediaType.APPLICATION_JSON)
          .statusCode(200)
          .body("name", equalTo("Zmieniona"))
          .extract()
          .path("hash");

      given()
          .header("Authorization", "Bearer " + token)
          .contentType("application/json")
          .body("""
            {
              "name": "SecondChange",
              "hash": "$hash"
            }
          """.replace("$hash", hash))
          .when()
          .put("/product/group/id/" + productGroup.getId())
          .then()
          .statusCode(200)
          .body("name", equalTo("SecondChange"));

      given()
          .header("Authorization", "Bearer " + token)
          .when()
          .get("/product/group/id/" + productGroup.getId())
          .then()
          .contentType(MediaType.APPLICATION_JSON)
          .statusCode(200)
          .body("name", equalTo("SecondChange"));
    }
  }

  @Nested
  @Order(2)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Negative {
    @Test
    @DisplayName("Should fail because of missing authorization header")
    @Order(1)
    void shouldFailBecauseOfMissingHeader() {
      given()
          .contentType("application/json")
          .body("""
            {
              "name": "Zmieniona",
              "hash": "$hash"
            }
          """.replace("$hash", CryptHashUtils.hashVersion(1L)))
          .when()
          .put("/product/group/id/50")
          .then()
          .statusCode(401);
    }

    @Test
    @DisplayName("Should fail because of invalid name given")
    @Order(2)
    void shouldFailBecauseOfInvalidName() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .contentType("application/json")
          .body("""
            {
              "name": "zmieniona",
              "hash": "$hash"
            }
          """.replace("$hash", CryptHashUtils.hashVersion(1L)))
          .when()
          .put("/product/group/id/50")
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("Should fail because of wrong hash given")
    @Order(3)
    void shouldFailBecauseOfWrongHash() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .contentType("application/json")
          .body("""
            {
              "name": "Zmieniona",
              "hash": "$hash"
            }
          """.replace("$hash", CryptHashUtils.hashVersion(15L)))
          .when()
          .put("/product/group/id/50")
          .then()
          .statusCode(409);
    }

    @Test
    @DisplayName("Should fail because of not existing id given")
    @Order(4)
    void shouldFailBecauseOfInvalidId() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .contentType("application/json")
          .body("""
            {
              "name": "Zmieniona",
              "hash": "$hash"
            }
          """.replace("$hash", CryptHashUtils.hashVersion(1L)))
          .when()
          .put("/product/group/id/" + Long.MAX_VALUE)
          .then()
          .statusCode(404);
    }

    @Test
    @DisplayName("Should fail because of missing body")
    @Order(5)
    void shouldFailBecauseOfMissingBody() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .contentType("application/json")
          .when()
          .put("/product/group/id/" + Long.MAX_VALUE)
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("Should fail because of wrong authorization header given")
    @Order(6)
    void shouldFailBecauseOfWrongHeader() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
          .contentType("application/json")
          .body("""
            {
              "name": "HarmonyHaven Double Bed",
              "hash": "$hash"
            }
          """.replace("$hash", CryptHashUtils.hashVersion(2L)))
          .when()
          .put("/product/group/id/50")
          .then()
          .statusCode(403);
    }

    @Test
    @DisplayName("Should fail because of missing name")
    @Order(7)
    void shouldFailBecauseOfMissingName() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .contentType("application/json")
          .body("""
            {
              "hash": "$hash"
            }
          """.replace("$hash", CryptHashUtils.hashVersion(2L)))
          .when()
          .put("/product/group/id/50")
          .then()
          .statusCode(400);
    }

    @Test
    @DisplayName("Should fail because of missing hash")
    @Order(8)
    void shouldFailBecauseOfMissingHash() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .contentType("application/json")
          .body("""
            {
              "name": "HarmonyHaven Double Bed"
            }
          """)
          .when()
          .put("/product/group/id/50")
          .then()
          .statusCode(400);
    }
  }
}
