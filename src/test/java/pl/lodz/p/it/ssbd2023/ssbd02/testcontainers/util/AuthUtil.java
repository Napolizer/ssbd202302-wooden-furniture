package pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util;

import static io.restassured.RestAssured.given;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import io.restassured.http.Header;

public class AuthUtil {
  private static Header acceptLanguageHeader = new Header("Accept-Language", "en-US");
  public static String retrieveToken(String login, String password) {
    return given()
        .contentType("application/json")
        .header(acceptLanguageHeader)
        .body("""
                   {
                       "login": "$login",
                       "password": "$password"
                   }
            """.replace("$login", login).replace("$password", password))
        .when()
        .post("/account/login")
        .then()
        .statusCode(200)
        .contentType("application/json")
        .extract()
        .path("token");
  }

  public static String retrieveToken(String role) {
    return switch (role) {
      case ADMINISTRATOR -> retrieveToken("administrator", "Student123!");
      case CLIENT -> retrieveToken("client", "Student123!");
      case SALES_REP -> retrieveToken("salesrep", "Student123!");
      case EMPLOYEE -> retrieveToken("employee", "Student123!");
      default -> throw new IllegalArgumentException("Unknown role: " + role);
    };
  }
}
