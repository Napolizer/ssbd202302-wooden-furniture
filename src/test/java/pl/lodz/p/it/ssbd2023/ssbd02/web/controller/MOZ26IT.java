package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.26 - Generate sales statistics")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ26IT {
  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Positive {
    @ParameterizedTest(name = "startDate: {0}, endDate: {1}")
    @Order(1)
    @DisplayName("Should properly generate sales statistics")
    @CsvSource({
        "06/01/2023, 06/30/2023",
        "01/01/2023, 06/30/2023",
        "01/01/2015, 12/31/2025",
    })
    void shouldProperlyGenerateSalesStatistics(String startDate, String endDate) {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveSalesRepToken())
          .queryParam("startDate", startDate)
          .queryParam("endDate", endDate)
          .get("/order/statistics")
          .then()
          .statusCode(200)
          .contentType(equalTo("application/json"))
          .body("productGroupName", is(notNullValue()))
          .body("averageRating", is(notNullValue()))
          .body("amount", is(notNullValue()))
          .body("soldAmount", is(notNullValue()))
          .body("totalIncome", is(notNullValue()));
    }
  }

  @Nested
  @Order(2)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Negative {
    @ParameterizedTest(name = "startDate: {0}, endDate: {1}")
    @Order(1)
    @DisplayName("Should fail to generate sales statistics with invalid date format")
    @CsvSource({
        "06/01/2023, 06-30-2023",
        "2023/01/06, 06/30/2023",
        "01/01/2015, 31/12/2025",
    })
    void shouldFailToGenerateSalesStatisticsWithInvalidDateFormat(String startDate, String endDate) {
      given()
          .header("Authorization", "Bearer " + InitData.retrieveSalesRepToken())
          .queryParam("startDate", startDate)
          .queryParam("endDate", endDate)
          .get("/order/statistics")
          .then()
          .statusCode(400)
          .body("message", equalTo(MessageUtil.MessageKey.INVALID_DATE));
    }
  }

  @Test
  @Order(2)
  @DisplayName("Should fail to generate sales statistics when the end date is before the start date")
  void shouldFailToGenerateSalesStatisticsWithEndDateBeforeStartDate() {
    given()
        .header("Authorization", "Bearer " + InitData.retrieveSalesRepToken())
        .queryParam("startDate", "12/01/2023")
        .queryParam("endDate", "01/01/2023")
        .get("/order/statistics")
        .then()
        .statusCode(400)
        .body("message", equalTo(MessageUtil.MessageKey.INVALID_DATE));
  }

  @Test
  @Order(3)
  @DisplayName("Should fail to generate sales statistics when authorization header is missing")
  void shouldFailBecauseOfMissingHeader() {
    given()
        .queryParam("startDate", "01/01/2023")
        .queryParam("endDate", "12/01/2023")
        .get("/order/statistics")
        .then()
        .statusCode(401);
  }

  @Test
  @Order(4)
  @DisplayName("Should fail to generate sales statistics when wrong authorization header is given")
  void shouldFailBecauseOfWrongHeader() {
    given()
        .header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
        .queryParam("startDate", "01/01/2023")
        .queryParam("endDate", "12/01/2023")
        .get("/order/statistics")
        .then()
        .statusCode(403);
  }

  @Test
  @Order(5)
  @DisplayName("Should fail to generate sales statistics when query params are missing")
  void shouldFailBecauseOfMissingQueryParams() {
    given()
        .header("Authorization", "Bearer " + InitData.retrieveSalesRepToken())
        .get("/order/statistics")
        .then()
        .statusCode(400);

    given()
        .header("Authorization", "Bearer " + InitData.retrieveSalesRepToken())
        .queryParam("startDate", "01/01/2023")
        .get("/order/statistics")
        .then()
        .statusCode(400);

    given()
        .header("Authorization", "Bearer " + InitData.retrieveSalesRepToken())
        .queryParam("endDate", "12/01/2023")
        .get("/order/statistics")
        .then()
        .statusCode(400);
  }
}
