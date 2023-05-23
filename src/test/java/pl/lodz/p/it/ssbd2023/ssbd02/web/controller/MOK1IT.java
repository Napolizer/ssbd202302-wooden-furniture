package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.1 - Register new account")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK1IT {

	@Nested
	@Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class Positive {
		@Test
		@Order(1)
		@DisplayName("Should properly register new account with client role")
		void shouldProperlyRegisterClientAccount() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(201);

			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.when()
							.get("/account/login/Register123")
							.then()
							.statusCode(200)
							.contentType("application/json")
							.body("accountState", equalTo("NOT_VERIFIED"))
							.body("roles", hasSize(1))
							.body("roles[0]", equalTo("client"));
		}

		@Test
		@Order(2)
		@DisplayName("Should properly register new account with client role and company")
		void shouldProperlyRegisterClientAccountWithCompany() {
			AccountRegisterDto account = InitData.getAccountWithCompany();
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(201);
		}

		@ParameterizedTest(name = "timeZone: {0}, login: {1}")
		@Order(3)
		@DisplayName("Should properly register new account with each timezone")
		@CsvSource({
						"EUROPE_WARSAW,europewarsaw",
						"AMERICA_NEW_YORK,americanewyork",
						"AMERICA_LOS_ANGELES,americelosangeles",
						"ASIA_TOKYO,asiatokyo",
						"AUSTRALIA_SYDNEY,australiasydney",
						"EUROPE_LONDON,europelondon",
						"EUROPE_BERLIN,europeberlin",
						"AMERICA_CHICAGO,americachicago",
						"ASIA_SHANGHAI,asiashanghai",
						"AMERICA_SAO_PAULO,americasaopaulo"

		})
		void shouldProperlyRegisterClientAccountWithEachTimeZone(String timeZone, String login) {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setLogin(login + "123");
			account.setEmail(login + "@example.com");
			account.setTimeZone(timeZone);
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(201);
		}
	}

	@Nested
	@Order(2)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class Negative {
		@Test
		@Order(1)
		@DisplayName("Should fail to register account with existing company NIP")
		void shouldFailToRegisterAccountWithExistingCompanyNip() {
			AccountRegisterDto account = InitData.getAccountWithCompany();
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(409);

			account.setEmail("different123@example.com");
			account.setLogin("Different123");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(409)
							.body("message", equalTo(MessageUtil.MessageKey.COMPANY_NIP_ALREADY_EXISTS));
		}

		@Test
		@Order(2)
		@DisplayName("Should fail to register account with existing account email")
		void shouldFailToRegisterAccountWithExistingEmail() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(409);

			account.setLogin("Different123");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(409)
							.body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_EMAIL_ALREADY_EXISTS));
		}

		@Test
		@Order(3)
		@DisplayName("Should fail to register account with existing account login")
		void shouldFailToRegisterAccountWithExistingLogin() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setEmail("different123@example.com");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(409)
							.body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_LOGIN_ALREADY_EXISTS));
		}

		@Test
		@Order(4)
		@DisplayName("Should fail to register account with empty data")
		void shouldFailToRegisterAccountWithEmptyData() {
			AccountRegisterDto account = AccountRegisterDto.builder().build();
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(12));
		}

		@Test
		@Order(5)
		@DisplayName("Should fail to register account with invalid email format")
		void shouldFailToRegisterAccountWithInvalidEmailFormat() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setEmail("invalidEmail");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("email"))
							.body("errors[0].message", equalTo("Invalid email address format"));
		}

		@Test
		@Order(6)
		@DisplayName("Should fail to register account with invalid login pattern")
		void shouldFailToRegisterAccountWithInvalidLoginPattern() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setLogin("login123_");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("login"))
							.body("errors[0].message",
											equalTo("Login must start with a letter and contain only letters and digits."));

			account.setLogin("Login_123");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("login"))
							.body("errors[0].message",
											equalTo("Login must start with a letter and contain only letters and digits."));

			account.setLogin("123Login");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("login"))
							.body("errors[0].message",
											equalTo("Login must start with a letter and contain only letters and digits."));
		}

		@Test
		@Order(7)
		@DisplayName("Should fail to register account with too short login")
		void shouldFailToRegisterAccountWithTooShortLogin() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setLogin("Joe11");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("login"))
							.body("errors[0].message",
											equalTo("The length of the field must be between 6 and 20 characters"));

		}

		@Test
		@Order(8)
		@DisplayName("Should fail to register account with too long login")
		void shouldFailToRegisterAccountWithTooLongLogin() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setLogin("Joe11joe11joe11joe11x");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("login"))
							.body("errors[0].message",
											equalTo("The length of the field must be between 6 and 20 characters"));
		}

		@Test
		@Order(9)
		@DisplayName("Should fail to register account with invalid password pattern")
		void shouldFailToRegisterAccountWithWithInvalidPasswordPattern() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setPassword("password123");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("password"))
							.body("errors[0].message", equalTo("Password must contain at least " +
											"one uppercase letter and one special character from the following set: !@#$%^&+="));

			account.setPassword("password!!!");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("password"))
							.body("errors[0].message", equalTo("Password must contain at least " +
											"one uppercase letter and one special character from the following set: !@#$%^&+="));

			account.setPassword("password123!");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("password"))
							.body("errors[0].message", equalTo("Password must contain at least " +
											"one uppercase letter and one special character from the following set: !@#$%^&+="));

			account.setPassword("Password123");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("password"))
							.body("errors[0].message", equalTo("Password must contain at least " +
											"one uppercase letter and one special character from the following set: !@#$%^&+="));

		}

		@Test
		@Order(10)
		@DisplayName("Should fail to register account with too short password")
		void shouldFailToRegisterAccountWithTooShortPassword() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setPassword("Pass!!!");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("password"))
							.body("errors[0].message",
											equalTo("The length of the field must be between 8 and 32 characters"));
		}

		@Test
		@Order(11)
		@DisplayName("Should fail to register account with too long password")
		void shouldFailToRegisterAccountWithTooLongPassword() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setPassword("Pass123!Pass123!Pass123!Pass123!!");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("password"))
							.body("errors[0].message",
											equalTo("The length of the field must be between 8 and 32 characters"));
		}

		@Test
		@Order(12)
		@DisplayName("Should fail to register account with invalid capitalized field pattern")
		void shouldFailToRegisterAccountWithWithInvalidCapitalizedPattern() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setFirstName("joe");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("firstName"))
							.body("errors[0].message",
											equalTo("Field must start with a capital letter and contain only letters"));

			account.setFirstName("Joe123");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("firstName"))
							.body("errors[0].message",
											equalTo("Field must start with a capital letter and contain only letters"));

			account.setFirstName("Joe!");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("firstName"))
							.body("errors[0].message",
											equalTo("Field must start with a capital letter and contain only letters"));
		}

		@Test
		@Order(13)
		@DisplayName("Should fail to register account with empty capitalized field pattern")
		void shouldFailToRegisterAccountWithEmptyCapitalized() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setFirstName("");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(2))
							.body("errors[0].field", equalTo("firstName"))
							.body("errors[1].field", equalTo("firstName"));

		}

		@Test
		@Order(14)
		@DisplayName("Should fail to register account with too long capitalized field pattern")
		void shouldFailToRegisterAccountWithTooLongCapitalized() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setFirstName("John DoeBo John DoeBo");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("firstName"))
							.body("errors[0].message",
											equalTo("The length of the field must be between 1 and 20 characters"));
		}

		@Test
		@Order(15)
		@DisplayName("Should fail to register account with invalid locale pattern")
		void shouldFailToRegisterAccountWithInvalidLocalePattern() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setLocale("pl_PL");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("locale"))
							.body("errors[0].message",
											equalTo("Locale must contain only two lowercase letters"));

			account.setLocale("PL");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("locale"))
							.body("errors[0].message",
											equalTo("Locale must contain only two lowercase letters"));

			account.setLocale("pl_pl");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("locale"))
							.body("errors[0].message",
											equalTo("Locale must contain only two lowercase letters"));
		}

		@Test
		@Order(16)
		@DisplayName("Should fail to register account with invalid postal code pattern")
		void shouldFailToRegisterAccountWithInvalidPostalCodePattern() {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setPostalCode("90222");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("postalCode"))
							.body("errors[0].message",
											equalTo("Postal code must be in the format xx-xxx"));

			account.setPostalCode("902-22");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("postalCode"))
							.body("errors[0].message",
											equalTo("Postal code must be in the format xx-xxx"));

			account.setPostalCode("902-222");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("postalCode"))
							.body("errors[0].message",
											equalTo("Postal code must be in the format xx-xxx"));
		}

		@Test
		@Order(17)
		@DisplayName("Should fail to register account with invalid company nip pattern")
		void shouldFailToRegisterAccountWithInvalidCompanyNipPattern() {
			AccountRegisterDto account = InitData.getAccountWithCompany();
			account.setNip("111111111z");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("nip"))
							.body("errors[0].message",
											equalTo("NIP must contain 10 digits"));
		}

		@Test
		@Order(18)
		@DisplayName("Should fail to register account with too short company nip")
		void shouldFailToRegisterAccountWithTooShortCompanyNip() {
			AccountRegisterDto account = InitData.getAccountWithCompany();
			account.setNip("111111111");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("nip"))
							.body("errors[0].message",
											equalTo("NIP must contain 10 digits"));
		}

		@Test
		@Order(19)
		@DisplayName("Should fail to register account with too long company nip")
		void shouldFailToRegisterAccountWithTooLongCompanyNip() {
			AccountRegisterDto account = InitData.getAccountWithCompany();
			account.setNip("11111111111");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("nip"))
							.body("errors[0].message",
											equalTo("NIP must contain 10 digits"));
		}

		@Test
		@Order(20)
		@DisplayName("Should fail to register account with invalid company name pattern")
		void shouldFailToRegisterAccountWithInvalidCompanyNamePattern() {
			AccountRegisterDto account = InitData.getAccountWithCompany();
			account.setCompanyName("11Company");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("companyName"))
							.body("errors[0].message",
											equalTo("Field must start with a capital letter"));

			account.setCompanyName("New/Company");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("companyName"))
							.body("errors[0].message",
											equalTo("Field must start with a capital letter"));

			account.setCompanyName("company123");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("companyName"))
							.body("errors[0].message",
											equalTo("Field must start with a capital letter"));
		}

		@Test
		@Order(21)
		@DisplayName("Should fail to register account with empty company name")
		void shouldFailToRegisterAccountWithEmptyCompanyName() {
			AccountRegisterDto account = InitData.getAccountWithCompany();
			account.setCompanyName("");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(2))
							.body("errors[0].field", equalTo("companyName"))
							.body("errors[1].field", equalTo("companyName"));
		}

		@Test
		@Order(22)
		@DisplayName("Should fail to register account with too long company name")
		void shouldFailToRegisterAccountWithTooLongCompanyName() {
			AccountRegisterDto account = InitData.getAccountWithCompany();
			account.setCompanyName("CompanyNCompanyNCompanyNCompanyN1");
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("errors", hasSize(1))
							.body("errors[0].field", equalTo("companyName"))
							.body("errors[0].message",
											equalTo("The length of the field must be between 1 and 32 characters"));
		}


		@Order(23)
		@DisplayName("Should fail to register account with invalid timezones")
		@ParameterizedTest(name = "timeZone: {0}")
		@CsvSource({
						"EUROPE/WARSAW",
						"Europe/Warsaw",
						"Europe-Warsaw",
		})
		void shouldFailToRegisterAccountWithInvalidTimeZones(String timeZone) {
			AccountRegisterDto account = InitData.getAccountToRegister();
			account.setTimeZone(timeZone);
			given()
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/register")
							.then()
							.statusCode(400)
							.body("message", equalTo(MessageUtil.MessageKey.ERROR_INVALID_TIME_ZONE));
		}

		@Order(24)
		@DisplayName("Should only create one account with same login")
		@Test
		void shouldOnlyCreateOneAccountWithSameLogin() throws Exception {
			// Create 10 threads creating account
			ExecutorService executorService = Executors.newFixedThreadPool(10);
			List<Future<Response>> futures = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				futures.add(executorService.submit(() -> {
					AccountRegisterDto account = InitData.getAccountToRegisterForMultipleThreads();
					return given()
									.contentType("application/json")
									.body(InitData.mapToJsonString(account))
									.when()
									.post("/account/register");
				}));
			}

			// Wait for 10 threads to stop
			int successResponses = 0;
			for (Future<Response> future : futures) {
				successResponses += future.get().statusCode() == 201 ? 1 : 0;
			}

			// Check if only one account was created
			assertThat(successResponses, is(equalTo(1)));
		}
	}
}
