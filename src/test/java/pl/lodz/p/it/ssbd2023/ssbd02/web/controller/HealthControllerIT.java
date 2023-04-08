package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

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
