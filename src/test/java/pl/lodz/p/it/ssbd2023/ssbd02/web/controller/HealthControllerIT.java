package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
public class HealthControllerIT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }
  @Test
  public void appIsHealthyTest() {
    given()
        .when()
        .get("/health")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType("text/plain")
        .body(equalTo("OK"));
  }
}
