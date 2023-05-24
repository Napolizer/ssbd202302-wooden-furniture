package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.32 - Change mode")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK32IT {

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
}
