package pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util;

import static io.restassured.RestAssured.given;

import io.restassured.http.Header;

public class AccountUtil {
  private static final Header acceptLanguageHeader = new Header("Accept-Language", "en-US");

  public static int getAccountId(String login) {
    return given()
        .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator"))
        .header(acceptLanguageHeader)
        .when()
        .get("/account/login/" + login)
        .then()
        .statusCode(200)
        .extract()
        .path("id");
  }

  public static int registerUser(String login) {
    given()
        .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator"))
        .header("Content-Type", "application/json")
        .body("""
            {
                "login": "%s",
                "password": "Student123!",
                "email": "%s",
                "firstName": "John",
                "lastName": "Doe",
                "phoneNumber": "123456789",
                "country": "Poland",
                "city": "Lodz",
                "street": "Piotrkowska",
                "streetNumber": "1",
                "postalCode": "90-000",
                "locale": "en",
                "language": "en-US",
                "timeZone": "EUROPE_WARSAW"
            }
            """.formatted(login, login + "@ssbd.com"))
        .when()
        .post("/account/register")
        .then()
        .statusCode(201);
    return getAccountId(login);
  }

  public static int countAccounts() {
    return given()
        .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator"))
        .header(acceptLanguageHeader)
        .when()
        .get("/account")
        .then()
        .statusCode(200)
        .extract()
        .path("size()");
  }
}
