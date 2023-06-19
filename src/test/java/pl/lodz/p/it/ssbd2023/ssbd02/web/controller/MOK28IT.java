package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.28 - Refresh token")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK28IT {
  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080/api/v1";
  }
  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {
    @Order(1)
    @DisplayName("should properly retrieve token from refresh token")
    @ParameterizedTest(name = "login: {0}, password: {1}")
    @CsvSource({
        "client,Student123!",
        "salesrep,Student123!",
        "employee,Student123!",
        "clientemployee,Student123!"
    })
    void shouldProperlyRetrieveTokenFromRefreshToken(String login, String password) {
      String refreshToken = AuthUtil.retrieveRefreshToken(login, password);
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer %s".formatted(AuthUtil.retrieveToken(login, password)))
          .when()
          .get("/account/token/refresh/%s".formatted(refreshToken))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("token", is(notNullValue()));
    }

    @Order(2)
    @DisplayName("should properly access protected endpoint with retrieved token")
    @ParameterizedTest(name = "login: {0}, password: {1}")
    @CsvSource({
        "client,Student123!",
        "salesrep,Student123!",
        "employee,Student123!",
        "clientemployee,Student123!"
    })
    void shouldProperlyAccessProtectedEndpointWithRetrievedToken(String login, String password) {
      String refreshToken = AuthUtil.retrieveRefreshToken(login, password);
      String newToken = given()
          .contentType("application/json")
          .header("Authorization", "Bearer %s".formatted(AuthUtil.retrieveToken(login, password)))
          .when()
          .get("/account/token/refresh/%s".formatted(refreshToken))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("token", is(notNullValue()))
          .extract()
          .path("token");
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer %s".formatted(newToken))
          .when()
          .get("/account/self")
          .then()
          .statusCode(200)
          .contentType("application/json");
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {
    @Order(1)
    @DisplayName("should fail to retrieve token without providing bearer token")
    @Test
    void shouldFailToRetrieveTokenWithoutProvidingBearerToken() {
      String refreshToken = AuthUtil.retrieveRefreshToken("client", "Student123!");
      given()
          .contentType("application/json")
          .when()
          .get("/account/token/refresh/%s".formatted(refreshToken))
          .then()
          .statusCode(401);
    }

    @Order(2)
    @DisplayName("should fail to retrieve token with invalid bearer")
    @ParameterizedTest(name = "invalid bearer: {0}")
    @CsvSource({
        "invalidToken",
        "blabla",
        "123"
    })
    void shouldFailToRetrieveTokenWithInvalidBearer(String invalidBearer) {
      String refreshToken = AuthUtil.retrieveRefreshToken("client", "Student123!");
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer %s".formatted(invalidBearer))
          .when()
          .get("/account/token/refresh/%s".formatted(refreshToken))
          .then()
          .statusCode(401);
    }

    @Order(3)
    @DisplayName("should fail to retrieve token with invalid refresh token")
    @ParameterizedTest(name = "invalid refresh token: {0}")
    @CsvSource({
        "invalidRefreshToken",
        "blabla",
        "123"
    })
    void shouldFailToRetrieveTokenWithInvalidRefreshToken(String invalidRefreshToken) {
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer %s".formatted(AuthUtil.retrieveToken("client", "Student123!")))
          .when()
          .get("/account/token/refresh/%s".formatted(invalidRefreshToken))
          .then()
          .statusCode(401)
          .contentType("application/json")
          .body("message", is(equalTo("exception.invalid.refresh.token")));
    }

    @Order(4)
    @DisplayName("should fail to retrieve token with expired refresh token")
    @ParameterizedTest(name = "expired refresh token: {0}")
    @CsvSource({
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbmlzdHJhdG9yIiwiaWF0IjoxNjg0ODM4NjE4LCJleHAiOjE2ODQ4Mzg2MTh9.L6t55xLkCciNUkauZs_dui9eqwzpqgWI6zZQZs2QyTX0q8nln5rxUg6kP1zBhYR2jNexsHi3bvT3phmG59SUqQ",
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbmlzdHJhdG9yIiwiaWF0IjoxNjg0ODM4NjM5LCJleHAiOjE2ODQ4Mzg2Mzl9.JdrkICc5o6sC7mwBeemZpgRRbYBzcv0gM2PkNz_VNGTa0SdoVoDVrcY4zezU7pnwKBZyBpglgX2S4wqqe5U0rg",
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbmlzdHJhdG9yIiwiaWF0IjoxNjg0ODM4NjUzLCJleHAiOjE2ODQ4Mzg2NTR9.hD53MqQJ_UtenbBhZFt9f9PtEHlcwHBHG7vYdiifu500Ebc8QUk045OWlY2jmai5ViKk95XpqfErjntOXOKGKw"
    })
    void shouldFailToRetrieveTokenWithExpiredRefreshToken(String expiredRefreshToken) {
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer %s".formatted(AuthUtil.retrieveToken("administrator", "Student123!")))
          .when()
          .get("/account/token/refresh/%s".formatted(expiredRefreshToken))
          .then()
          .statusCode(401)
          .contentType("application/json")
          .body("message", is(equalTo("exception.expired.refresh.token")));
    }

    @Order(5)
    @DisplayName("should fail to retrieve token with another user refresh token")
    @ParameterizedTest(name = "another user login: {0}")
    @CsvSource({
        "client",
        "employee",
        "clientemployee",
        "salesrep",
    })
    void shouldFailToRetrieveTokenWithAnotherUserRefreshToken(String login) {
      String refreshToken = AuthUtil.retrieveRefreshToken(login, "Student123!");
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer %s".formatted(AuthUtil.retrieveToken("administrator", "Student123!")))
          .when()
          .get("/account/token/refresh/%s".formatted(refreshToken))
          .then()
          .statusCode(403)
          .contentType("application/json")
          .body("message", is(equalTo("exception.forbidden")));
    }

    @Order(6)
    @DisplayName("should fail to retrieve token when providing refresh as bearer")
    @Test
    void shouldFailToRetrieveTokenWhenProvidingRefreshAsBearer() {
      String refreshToken = AuthUtil.retrieveRefreshToken("client", "Student123!");
      given()
          .contentType("application/json")
          .header("Authorization", "Bearer %s".formatted(refreshToken))
          .when()
          .get("/account/token/refresh/%s".formatted(refreshToken))
          .then()
          .statusCode(401);
    }
  }
}
