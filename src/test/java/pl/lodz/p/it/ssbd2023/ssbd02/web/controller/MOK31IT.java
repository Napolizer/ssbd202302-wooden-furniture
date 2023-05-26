package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.SortBy;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountSearchSettingsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.31 - Search by full name with pagination")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK31IT {

  @Nested
  @Order(1)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {

    @Order(1)
    @DisplayName("Should properly create accounts")
    @ParameterizedTest(name = "login: {0}, first name: {1}, last name: {2}")
    @CsvSource({
        "aaronr, Aaron, Ramsdale",
        "lukamo, Luka, Modric",
        "romelol, Romelo, Lukaku",
        "zimmerh, Zimmer, Hans"
    })
    void shouldProperlyCreateAccountWithEmployeeAccessLevelToEditAccessLevels(String login, String firstName, String lastName) {
      AccountUtil.createUserWithSpecifiedLoginFirstNameAndLastName(login, firstName, lastName);
    }

    @Order(2)
    @DisplayName(("Should properly get accounts with default pagination settings"))
    @Test
    void shouldProperlyGetAccountsWithDefaultPaginationSettings() {
      AccountSearchSettingsDto accountSearchSettingsDto = new AccountSearchSettingsDto();
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .contentType("application/json")
          .body(InitData.mapToJsonString(accountSearchSettingsDto))
          .when()
          .post("/account/find/fullNameWithPagination")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("size()", equalTo(accountSearchSettingsDto.getDisplayedAccounts()))
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
    @DisplayName("Should properly get specified number of accounts")
    @ParameterizedTest(name = "displayed accounts: {0}")
    @CsvSource({
        "3",
        "5",
        "7",
        "10"
    })
    void shouldProperlyGetSpecifiedNumberOfAccounts(int displayedAccounts) {
      AccountSearchSettingsDto accountSearchSettingsDto = new AccountSearchSettingsDto();
      accountSearchSettingsDto.setDisplayedAccounts(displayedAccounts);
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .contentType("application/json")
          .body(InitData.mapToJsonString(accountSearchSettingsDto))
          .when()
          .post("/account/find/fullNameWithPagination")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("size()", equalTo(displayedAccounts))
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

    @Order(4)
    @DisplayName("Should properly get account with specified keyword")
    @ParameterizedTest(name = "phrase: {0}")
    @CsvSource({
        "Aaron Ramsdale",
        "Luka Modric",
        "Romelo Lukaku",
        "Zimmer Hans"
    })
    void shouldProperlyGetAccountWithSpecifiedKeyword(String keyword) {
      AccountSearchSettingsDto accountSearchSettingsDto = new AccountSearchSettingsDto();
      accountSearchSettingsDto.setSearchKeyword(keyword);
      String[] fullName = keyword.split(" ", 2);
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .contentType("application/json")
          .body(InitData.mapToJsonString(accountSearchSettingsDto))
          .when()
          .post("/account/find/fullNameWithPagination")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("size()", equalTo(1))
          .body("[0].id", is(notNullValue()))
          .body("[0].login", is(notNullValue()))
          .body("[0].email", is(notNullValue()))
          .body("[0].firstName", is(fullName[0]))
          .body("[0].lastName", is(fullName[1]))
          .body("[0].archive", is(notNullValue()))
          .body("[0].locale", is(notNullValue()))
          .body("[0].failedLoginCounter", is(notNullValue()))
          .body("[0].accountState", is(notNullValue()))
          .body("[0].roles", is(notNullValue()))
          .body("[0].address", is(notNullValue()))
          .body("[0].hash", is(notNullValue()));
    }

    @Order(5)
    @DisplayName("Should properly get account with specified sort by")
    @ParameterizedTest(name = "sort by string: {0}")
    @CsvSource({
        "LOGIN",
        "EMAIL",
        "ACCESSLEVEL"
    })
    void shouldProperlyGetAccountsWithSpecifiedSortBy(String sortByString) {
      SortBy sortBy = SortBy.valueOf(sortByString);
      AccountSearchSettingsDto accountSearchSettingsDto = new AccountSearchSettingsDto();
      accountSearchSettingsDto.setSortBy(sortBy);

      switch (sortByString) {
        case "LOGIN" -> given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
            .contentType("application/json")
            .body(InitData.mapToJsonString(accountSearchSettingsDto))
            .when()
            .post("/account/find/fullNameWithPagination")
            .then()
            .statusCode(200)
            .contentType("application/json")
            .body("size()", equalTo(accountSearchSettingsDto.getDisplayedAccounts()))
            .body("[0].id", is(notNullValue()))
            .body("[0].login", is("aaronr"))
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
        case "EMAIL" -> given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
            .contentType("application/json")
            .body(InitData.mapToJsonString(accountSearchSettingsDto))
            .when()
            .post("/account/find/fullNameWithPagination")
            .then()
            .statusCode(200)
            .contentType("application/json")
            .body("size()", equalTo(accountSearchSettingsDto.getDisplayedAccounts()))
            .body("[0].id", is(notNullValue()))
            .body("[0].login", is(notNullValue()))
            .body("[0].email", is("aaronr@ssbd.com"))
            .body("[0].firstName", is(notNullValue()))
            .body("[0].lastName", is(notNullValue()))
            .body("[0].archive", is(notNullValue()))
            .body("[0].locale", is(notNullValue()))
            .body("[0].failedLoginCounter", is(notNullValue()))
            .body("[0].accountState", is(notNullValue()))
            .body("[0].roles", is(notNullValue()))
            .body("[0].address", is(notNullValue()))
            .body("[0].hash", is(notNullValue()));
        case "ACCESSLEVEL" -> given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
            .contentType("application/json")
            .body(InitData.mapToJsonString(accountSearchSettingsDto))
            .when()
            .post("/account/find/fullNameWithPagination")
            .then()
            .statusCode(200)
            .contentType("application/json")
            .body("size()", equalTo(accountSearchSettingsDto.getDisplayedAccounts()))
            .body("[0].id", is(notNullValue()))
            .body("[0].login", is("administrator"))
            .body("[0].email", is("admin@gmail.com"))
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
    }

    @Order(6)
    @DisplayName("Should properly get accounts with descending sorting")
    @Test
    void shouldProperlyGetAccountsWithDescendingSorting() {
      AccountSearchSettingsDto accountSearchSettingsDto = new AccountSearchSettingsDto();
      accountSearchSettingsDto.setSortAscending(false);
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .contentType("application/json")
          .body(InitData.mapToJsonString(accountSearchSettingsDto))
          .when()
          .post("/account/find/fullNameWithPagination")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("size()", equalTo(accountSearchSettingsDto.getDisplayedAccounts()))
          .body("[0].id", is(notNullValue()))
          .body("[0].login", is("zimmerh"))
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

    @Order(7)
    @DisplayName("Should properly get accounts without specified settings")
    @Test
    void shouldProperlyGetAccountsWithoutSpecifiedSettings() {
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .contentType("application/json")
          .body("{}")
          .when()
          .post("/account/find/fullNameWithPagination")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("size()", equalTo(10))
          .body("[0].id", is(notNullValue()))
          .body("[0].login", is("aaronr"))
          .body("[0].email", is("aaronr@ssbd.com"))
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
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {

    @Order(1)
    @DisplayName(("Should fail to get accounts with specified settings without token"))
    @Test
    void shouldFailToGetAccountsWithSpecifiedSettingsWithoutToken() {
      AccountSearchSettingsDto accountSearchSettingsDto = new AccountSearchSettingsDto();
      given()
          .contentType("application/json")
          .body(InitData.mapToJsonString(accountSearchSettingsDto))
          .when()
          .post("/account/find/fullNameWithPagination")
          .then()
          .statusCode(401);
    }

    @Order(2)
    @DisplayName(("Should fail to get accounts with specified settings without permission"))
    @Test
    void shouldFailToGetAccountsWithSpecifiedSettingsWithoutPermission() {
      AccountSearchSettingsDto accountSearchSettingsDto = new AccountSearchSettingsDto();
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("client", "Student123!"))
          .contentType("application/json")
          .body(InitData.mapToJsonString(accountSearchSettingsDto))
          .when()
          .post("/account/find/fullNameWithPagination")
          .then()
          .statusCode(403);
    }

    @Order(3)
    @DisplayName(("Should fail to get accounts with wrong search page"))
    @Test
    void shouldFailToGetAccountsWithWrongSearchPage() {
      AccountSearchSettingsDto accountSearchSettingsDto = new AccountSearchSettingsDto();
      accountSearchSettingsDto.setSearchPage(-2);
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .contentType("application/json")
          .body(InitData.mapToJsonString(accountSearchSettingsDto))
          .when()
          .post("/account/find/fullNameWithPagination")
          .then()
          .statusCode(400);
    }

    @Order(4)
    @DisplayName(("Should fail to get accounts with wrong displayed accounts"))
    @Test
    void shouldFailToGetAccountsWithWrongDisplayedAccounts() {
      AccountSearchSettingsDto accountSearchSettingsDto = new AccountSearchSettingsDto();
      accountSearchSettingsDto.setDisplayedAccounts(-2);
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .contentType("application/json")
          .body(InitData.mapToJsonString(accountSearchSettingsDto))
          .when()
          .post("/account/find/fullNameWithPagination")
          .then()
          .statusCode(400);
    }
  }
}
