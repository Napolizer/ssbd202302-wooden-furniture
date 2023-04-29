package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class HealthControllerIT {
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
