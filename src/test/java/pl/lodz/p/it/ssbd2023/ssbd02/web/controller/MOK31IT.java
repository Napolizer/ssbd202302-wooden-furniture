package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.31 - Change mode")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK31IT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }

  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Init {
    @Test
    @DisplayName("Should properly create account1")
    @Order(1)
    void shouldProperlyCreateAccount1() {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
          .contentType("application/json")
          .body(InitData.accountToChangeModeJson)
          .when()
          .post("/account/create")
          .then()
          .statusCode(201);
    }
  }
  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {
    @Order(1)
    @DisplayName("Should properly change mode")
    @Test
    void shouldProperlyChangeMode() {
      given()
          .header(HttpHeaders.AUTHORIZATION,"Bearer " + AuthUtil.retrieveToken("changeMode", "Password123!"))
          .contentType("application/json")
          .body("""
                     {
                         "mode": "dark"
                     }
              """)
          .when()
          .put("/account/self/change-mode")
          .then()
          .statusCode(200);
    }
  }

  @Nested
  @Order(3)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {
    @Order(1)
    @DisplayName("Should fail to change mode because of missing header")
    @Test
    void shouldFailBecauseOfMissingHeader() {
      given()
          .contentType("application/json")
          .body("""
                     {
                         "mode": "dark"
                     }
              """)
          .when()
          .put("/account/self/change-mode")
          .then()
          .statusCode(401);
    }

    @Order(2)
    @DisplayName("Should fail to change mode because of invalid mode given")
    @Test
    void shouldFailBecauseOfInvalidMode() {
      given()
          .header(HttpHeaders.AUTHORIZATION,"Bearer " + AuthUtil.retrieveToken("changeMode", "Password123!"))
          .contentType("application/json")
          .body("""
                     {
                         "mode": "invalid"
                     }
              """)
          .when()
          .put("/account/self/change-mode")
          .then()
          .statusCode(400);
    }

    @Order(3)
    @DisplayName("Should fail to change mode because missing body")
    @Test
    void shouldProperlyChangeMode() {
      given()
          .header(HttpHeaders.AUTHORIZATION,"Bearer " + AuthUtil.retrieveToken("changeMode", "Password123!"))
          .when()
          .put("/account/self/change-mode")
          .then()
          .statusCode(500);
    }
  }
}
