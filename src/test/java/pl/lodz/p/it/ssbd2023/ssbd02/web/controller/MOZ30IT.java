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
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.30 - Activate product group")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ30IT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }
  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Init {
    @Test
    @DisplayName("Should properly archive product group")
    @Order(1)
    void shouldProperlyArchiveProductGroup() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .when()
          .put("/product/group/archive/id/39")
          .then()
          .statusCode(200)
          .body("archive", equalTo(true));
    }
  }

  @Nested
  @Order(2)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Positive {
    @Test
    @DisplayName("Should properly activate product group")
    @Order(1)
    void shouldProperlyActivateProductGroup() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .when()
          .put("/product/group/activate/id/39")
          .then()
          .statusCode(200)
          .body("archive", equalTo(false));
    }
  }

  @Nested
  @Order(3)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Negative {
    @Test
    @DisplayName("Should fail because of missing authorization header")
    @Order(1)
    void shouldFailBecauseOfMissingHeader() {
      given()
          .when()
          .put("/product/group/activate/id/38")
          .then()
          .statusCode(401);
    }

    @Test
    @DisplayName("Should fail because of not existing id given")
    @Order(2)
    void shouldFailBecauseOfInvalidId() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .when()
          .put("/product/group/activate/id/" + Long.MAX_VALUE)
          .then()
          .statusCode(404);
    }
    @Test
    @DisplayName("Should fail to activate already activated product group")
    @Order(3)
    void shouldFailToArchiveProductGroupSecondTime() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
          .when()
          .put("/product/group/activate/id/39")
          .then()
          .statusCode(400);
    }
  }
}
