package pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util;

import static io.restassured.RestAssured.given;

import io.restassured.http.Header;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

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

  public static String getAccount(String login) {
    return given()
        .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator"))
        .header(acceptLanguageHeader)
        .when()
        .get("/account/login/" + login)
        .then()
        .statusCode(200)
        .extract()
        .body()
        .asPrettyString();
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

  public static int createUser(String login) {
    given()
        .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
        .contentType("application/json")
        .body("""
          {
              "firstName": "John",
              "lastName": "Boe",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Student123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Employee"
                },
              "login": "%s",
              "email": "%s",
              "timeZone": "EUROPE_WARSAW"
          }
      """.formatted(login, login + "@ssbd.com"))
        .when()
        .post("/account/create")
        .then()
        .statusCode(201);
    return getAccountId(login);
  }

  public static int createUserWithSpecifiedLoginFirstNameAndLastName(String login, String firstName, String lastName) {
    given()
        .header("Authorization", "Bearer " + InitData.retrieveAdminToken())
        .contentType("application/json")
        .body("""
          {
              "firstName": "%s",
              "lastName": "%s",
              "country": "Poland",
              "city": "Lodz",
              "street": "Karpacka",
              "streetNumber": 55,
              "postalCode": "93-539",
              "password": "Student123!",
              "locale": "pl",
              "accessLevel":
                {
                  "name": "Employee"
                },
              "login": "%s",
              "email": "%s",
              "timeZone": "EUROPE_WARSAW"
          }
      """.formatted(firstName, lastName, login, login + "@ssbd.com"))
        .when()
        .post("/account/create")
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
