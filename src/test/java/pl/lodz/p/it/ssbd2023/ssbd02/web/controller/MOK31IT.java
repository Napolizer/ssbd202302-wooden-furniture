package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountSearchSettings;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper.AccountSearchSettingsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
    @DisplayName(("Should properly get accounts with default pagination settings"))
    @Test
    void shouldProperlyGetAccountsWithDefaultPaginationSettings() {
      AccountSearchSettingsDto accountSearchSettingsDto = new AccountSearchSettingsDto();
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .contentType("application/json")
          .body(InitData.mapToJsonString(accountSearchSettingsDto))
          .when()
          .get("account/find/fullNameWithPagination")
          .then()
          .statusCode(200)
          .contentType("application/json")
          .body("size()", equalTo(10))
          .body("[0].id", is(equalTo("cos")))
          .body("[0].login", is(equalTo("administrator")))
          .body("[0].email", is(equalTo("cos")))
          .body("[0].firstName", is(equalTo("cos")))
          .body("[0].lastName", is(equalTo("cos")))
          .body("[0].archive", is(equalTo("cos")))
          .body("[0].locale", is(equalTo("cos")))
          .body("[0].failedLoginCounter", is(equalTo("cos")))
          .body("[0].accountState", is(equalTo("cos")))
          .body("[0].roles", is(equalTo("cos")))
          .body("[0].address", is(equalTo("cos")))
          .body("[0].hash", is(notNullValue()));
    }

    @Order(2)
    @DisplayName("Should properly get specified number of accounts")
    @ParameterizedTest(name = "displayed accounts: {0}")
    @CsvSource({
        "3",
        "5",
        "7",
        "10"
    })
    void shouldProperlyGetSpecifiedNumberOfAccounts(int displayedAccounts) {
      AccountSearchSettings accountSearchSettings = AccountSearchSettings
          .builder()
          .displayedAccounts(displayedAccounts)
          .build();
      accountSearchSettings.setSearchKeyword("a");
      given()
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
          .contentType("application/json")
          .body(InitData.mapToJsonString(accountSearchSettings))
          .when()
          .get("account/find/fullNameWithPagination")
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
  }
}
