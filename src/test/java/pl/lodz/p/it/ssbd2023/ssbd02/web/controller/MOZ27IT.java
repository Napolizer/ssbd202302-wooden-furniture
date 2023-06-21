package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOZ.27 - Generate sales report")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOZ27IT {

	@Nested
	@Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class Positive {
		@ParameterizedTest(name = "startDate: {0}, endDate: {1}")
		@Order(1)
		@DisplayName("Should properly generate report with valid date format")
		@CsvSource({
						"06/01/2023, 06/30/2023",
						"01/01/2023, 06/30/2023",
						"01/01/2015, 12/31/2025",
		})
		void shouldProperlyGenerateReport(String startDate, String endDate) {
			given()
							.header("Authorization", "Bearer " + InitData.retrieveSalesRepToken())
							.header("Accept-Language", MessageUtil.LOCALE_EN)
							.queryParam("startDate", startDate)
							.queryParam("endDate", endDate)
							.get("/order/report")
							.then()
							.statusCode(200)
							.contentType(equalTo("application/vnd.ms-excel"));

		}

	}

	@Nested
	@Order(2)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class Negative {
		@ParameterizedTest(name = "startDate: {0}, endDate: {1}")
		@Order(1)
		@DisplayName("Should fail to generate report with invalid date format")
		@CsvSource({
						"06/01/2023, 06-30-2023",
						"2023/01/06, 06/30/2023",
						"01/01/2015, 31/12/2025",
		})
		void shouldFailToGenerateReportWithInvalidDateFormat(String startDate, String endDate) {
			given()
							.header("Authorization", "Bearer " + InitData.retrieveSalesRepToken())
							.header("Accept-Language", MessageUtil.LOCALE_EN)
							.queryParam("startDate", startDate)
							.queryParam("endDate", endDate)
							.get("/order/report")
							.then()
							.statusCode(400)
							.body("message", equalTo(MessageUtil.MessageKey.INVALID_DATE));
		}

		@Test
		@Order(2)
		@DisplayName("Should fail to generate report when the end date is earlier than the start date")
		void shouldFailToGenerateReportWithEarlierEndDate() {
			given()
							.header("Authorization", "Bearer " + InitData.retrieveSalesRepToken())
							.header("Accept-Language", MessageUtil.LOCALE_EN)
							.queryParam("startDate", "12/01/2023")
							.queryParam("endDate", "01/01/2023")
							.get("/order/report")
							.then()
							.statusCode(400)
							.body("message", equalTo(MessageUtil.MessageKey.INVALID_DATE));
		}

		@ParameterizedTest(name = "locale: {0}")
		@Order(3)
		@DisplayName("Should fail to generate report with invalid accept language header")
		@CsvSource({
						"us",
						"de",
						"fr"
		})
		void shouldFailToGenerateReportWithInvalidAcceptLanguageHeader(String locale) {
			given()
							.header("Authorization", "Bearer " + InitData.retrieveSalesRepToken())
							.header("Accept-Language", locale)
							.queryParam("startDate", "01/01/2023")
							.queryParam("endDate", "12/01/2023")
							.get("/order/report")
							.then()
							.statusCode(400)
							.body("message", equalTo(MessageUtil.MessageKey.INVALID_LOCALE));
		}

		@Test
		@Order(4)
		@DisplayName("Should fail to generate report without authorization header")
		void shouldFailToGenerateReportWithoutAuthorizationHeader() {
			given()
							.header("Accept-Language", MessageUtil.LOCALE_PL)
							.queryParam("startDate", "01/01/2023")
							.queryParam("endDate", "12/01/2023")
							.get("/order/report")
							.then()
							.statusCode(401);
		}

		@Test
		@Order(5)
		@DisplayName("Should fail to generate report as employee")
		void shouldFailToGenerateReportAsEmployee() {
			given()
							.header("Authorization", "Bearer " + InitData.retrieveEmployeeToken())
							.header("Accept-Language", MessageUtil.LOCALE_PL)
							.queryParam("startDate", "01/01/2023")
							.queryParam("endDate", "12/01/2023")
							.get("/order/report")
							.then()
							.statusCode(403);
		}
	}
}
