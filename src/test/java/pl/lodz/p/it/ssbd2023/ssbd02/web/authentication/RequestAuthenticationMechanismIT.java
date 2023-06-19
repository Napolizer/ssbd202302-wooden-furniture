package pl.lodz.p.it.ssbd2023.ssbd02.web.authentication;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
public class RequestAuthenticationMechanismIT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }
  @Test
  private String retrieveAdminToken() {
    return given()
        .contentType("application/json")
        .header(HttpHeaders.ACCEPT_LANGUAGE, "pl")
        .body("""
                   {
                       "login": "administrator",
                       "password": "Student123!"
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
  void shouldProperlyLoginTest() {
    given()
        .contentType("application/json")
        .header(HttpHeaders.ACCEPT_LANGUAGE, "pl")
        .body("""
                   {
                       "login": "administrator",
                       "password": "Student123!"
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
  void shouldHaveAccessToAdminEndpointTest() {
    given()
        .header("Authorization", "Bearer " + retrieveAdminToken())
        .header(HttpHeaders.ACCEPT_LANGUAGE, "pl")
        .when()
        .get("/account")
        .then()
        .statusCode(200)
        .contentType("application/json");
  }

  @Test
  @Order(3)
  void shouldNotHaveAccessWhenTokenIsMissing() {
    given()
        .when()
        .get("/account")
        .then()
        .statusCode(401)
        .contentType("text/html");
  }

  @Test
  @Order(4)
  void shouldNotHaveAccessWhenTokenIsEmpty() {
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
  void shouldNotHaveAccessWhenTokenIsMissingBearer() {
    given()
        .header("Authorization", " " + retrieveAdminToken())
        .header(HttpHeaders.ACCEPT_LANGUAGE, "pl")
        .when()
        .get("/account")
        .then()
        .statusCode(401)
        .contentType("text/html");
  }

  @Test
  @Order(6)
  void shouldNotHaveAccessWhenTokenDoesNotHaveRequiredRole() {
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
