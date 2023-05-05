package pl.lodz.p.it.ssbd2023.ssbd02.web.authentication;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
public class RequestAuthenticationMechanismIT {
  private String retrieveAdminToken() {
    return given()
        .contentType("application/json")
        .body("""
                   {
                       "login": "administrator",
                       "password": "Kochamssbd!"
                   }
            """)
        .when()
        .post("/account/login")
        .then()
        .statusCode(200)
        .contentType("application/json")
        .extract()
        .path("token");
  }

  @Test
  @Order(1)
  public void shouldProperlyLoginTest() {
    given()
        .contentType("application/json")
        .body("""
                   {
                       "login": "administrator",
                       "password": "Kochamssbd!"
                   }
            """)
        .when()
        .post("/account/login")
        .then()
        .statusCode(200)
        .contentType("application/json")
        .body("token", notNullValue())
        .body("token", not(emptyString()));
  }

  @Test
  @Order(2)
  public void shouldHaveAccessToAdminEndpointTest() {
    given()
        .header("Authorization", "Bearer " + retrieveAdminToken())
        .when()
        .get("/account")
        .then()
        .statusCode(200)
        .contentType("application/json");
  }

  @Test
  @Order(3)
  public void shouldNotHaveAccessWhenTokenIsMissing() {
    given()
        .when()
        .get("/account")
        .then()
        .statusCode(401)
        .contentType("text/html");
  }

  @Test
  @Order(4)
  public void shouldNotHaveAccessWhenTokenIsEmpty() {
    given()
        .header("Authorization", "")
        .when()
        .get("/account")
        .then()
        .statusCode(401)
        .contentType("text/html");
  }

  @Test
  @Order(5)
  public void shouldNotHaveAccessWhenTokenIsMissingBearer() {
    given()
        .header("Authorization", " " + retrieveAdminToken())
        .when()
        .get("/account")
        .then()
        .statusCode(401)
        .contentType("text/html");
  }

  @Test
  @Order(6)
  public void shouldNotHaveAccessWhenTokenDoesNotHaveRequiredRole() {
    given()
        .header("Authorization", "Bearer " + retrieveAdminToken())
        .header("Content-Type", "application/json")
        .body("{}")
        .when()
        .post("/account/register")
        .then()
        .statusCode(400)
        .contentType("application/json");
  }
}
