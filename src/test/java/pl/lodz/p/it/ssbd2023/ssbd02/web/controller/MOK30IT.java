package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import jakarta.ws.rs.core.HttpHeaders;
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
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.30 - Search by full name")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK30IT {
  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {
    @Order(1)
    @DisplayName("should properly get list of all accounts which include specified full name")
    @ParameterizedTest(name = "full name: {0}, expected size: {1}")
    @CsvSource({
        "ad,1",
        "jul,1",
        "ki,3"
    })
    void shouldGetListOfAllAccountsWhichIncludeSpecifiedFullName(String fullName, int expectedSize) {
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .when()
          .get("/account/find/fullName/%s".formatted(fullName))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("size()", is(greaterThanOrEqualTo(expectedSize)))
          .body("[0].id", is(notNullValue()))
          .body("[0].login", is(notNullValue()))
          .body("[0].email", is(notNullValue()))
          .body("[0].firstName", is(notNullValue()))
          .body("[0].lastName", is(notNullValue()))
          .body("[0].archive", is(notNullValue()))
          .body("[0].locale", is(notNullValue()))
          .body("[0].failedLoginCounter", is(notNullValue()))
          .body("[0].accountState", is(notNullValue()))
          .body("[0].roles", is(notNullValue()))
          .body("[0].address", is(notNullValue()))
          .body("[0].hash", is(notNullValue()));
    }

    @Order(2)
    @DisplayName("should properly get list of all accounts which include specified full name ignoring case")
    @ParameterizedTest(name = "full name: {0}, expected size: {1}")
    @CsvSource({
        "aD,1",
        "JuL,1",
        "Ki,3"
    })
    void shouldGetListOfAllAccountsWhichIncludeSpecifiedFullNameIgnoringCase(String fullName, int expectedSize) {
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .when()
          .get("/account/find/fullName/%s".formatted(fullName))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("size()", is(greaterThanOrEqualTo(expectedSize)))
          .body("[0].id", is(notNullValue()))
          .body("[0].login", is(notNullValue()))
          .body("[0].email", is(notNullValue()))
          .body("[0].firstName", is(notNullValue()))
          .body("[0].lastName", is(notNullValue()))
          .body("[0].archive", is(notNullValue()))
          .body("[0].locale", is(notNullValue()))
          .body("[0].failedLoginCounter", is(notNullValue()))
          .body("[0].accountState", is(notNullValue()))
          .body("[0].roles", is(notNullValue()))
          .body("[0].address", is(notNullValue()))
          .body("[0].hash", is(notNullValue()));
    }

    @Order(3)
    @DisplayName("should return empty array if no users were found")
    @ParameterizedTest(name = "full name: {0}, expected size: {1}")
    @CsvSource({
        "asdasd",
        "12323231",
        "blikblik"
    })
    void shouldReturnEmptyArrayIfNoUsersWereFound(String fullName) {
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .when()
          .get("/account/find/fullName/%s".formatted(fullName))
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("size()", is(equalTo(0)));
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {
    @Order(1)
    @DisplayName("should fail if no token was provided")
    @Test
    void shouldFailIfNoTokenWasProvided() {
      given()
          .when()
          .get("/account/find/fullName/adam")
          .then()
          .statusCode(401);
    }

    @Order(2)
    @DisplayName("should fail if user is not administrator")
    @ParameterizedTest(name = "login: {0}")
    @CsvSource({
        "client",
        "clientemployee",
        "employee",
        "salesrep"
    })
    void shouldFailIfUserIsNotAdministrator(String login) {
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(login, "Student123!"))
          .when()
          .get("/account/find/fullName/adam")
          .then()
          .statusCode(403);
    }

    @Order(3)
    @DisplayName("should fail if no full name was specified")
    @Test
    void shouldFailIfNoFullNameWasSpecified() {
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .when()
          .get("/account/find/fullName/")
          .then()
          .statusCode(404);
    }
  }
}
