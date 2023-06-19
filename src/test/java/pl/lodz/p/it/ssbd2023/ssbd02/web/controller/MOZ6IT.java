package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.6 - Edit product group name")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ6IT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }
  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Positive {
    @Test
    @DisplayName("Should properly edit product group name")
    @Order(1)
    void shouldProperlyEditProductGroupName() {
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
          .put("/product/group/id/50")
          .then()
          .statusCode(200)
          .body("name", equalTo("Zmieniona"));
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
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
          .statusCode(200)
          .body("name", equalTo("HarmonyHaven Double Bed"));
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
  }
}
