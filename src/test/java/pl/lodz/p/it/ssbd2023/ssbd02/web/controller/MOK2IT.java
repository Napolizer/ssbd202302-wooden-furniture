package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.2 - Create new account")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK2IT {

	@Nested
	@Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class Positive {
		@Test
		@Order(1)
		@DisplayName("Should properly create new activated account")
		void shouldProperlyCreateActiveAccount() {
			AccountCreateDto account = InitData.getAccountToCreate();
			account.setLogin("Active123");
			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/create")
							.then()
							.statusCode(201);

			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.when()
							.get("/account/login/Active123")
							.then()
							.statusCode(200)
							.contentType("application/json")
							.body("accountState", equalTo("ACTIVE"))
							.body("roles", hasSize(1))
							.body("roles[0]", equalTo("administrator"));
		}

		@Test
		@Order(2)
		@DisplayName("Should properly create new activated account with client access level")
		void shouldProperlyCreateAccountWithClientAccessLevel() {
			AccountCreateDto account = InitData.getAccountToCreate();
			account.setLogin("Client123");
			account.setEmail("client123@example.com");
			account.setNip("9999999999");
			account.setAccessLevel(new AccessLevelDto("client"));
			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/create")
							.then()
							.statusCode(201);

			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.when()
							.get("/account/login/Client123")
							.then()
							.statusCode(200)
							.contentType("application/json")
							.body("accountState", equalTo("ACTIVE"))
							.body("roles", hasSize(1))
							.body("roles[0]", equalTo("client"));
		}

		@Test
		@Order(3)
		@DisplayName("Should properly create new activated account with employee access level")
		void shouldProperlyCreateAccountWithEmployeeAccessLevel() {
			AccountCreateDto account = InitData.getAccountToCreate();
			account.setLogin("Employee123");
			account.setEmail("employee123@example.com");
			account.setAccessLevel(new AccessLevelDto("employee"));
			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/create")
							.then()
							.statusCode(201);

			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.when()
							.get("/account/login/Employee123")
							.then()
							.statusCode(200)
							.contentType("application/json")
							.body("accountState", equalTo("ACTIVE"))
							.body("roles", hasSize(1))
							.body("roles[0]", equalTo("employee"));
		}

		@Test
		@Order(4)
		@DisplayName("Should properly create new activated account with administrator access level")
		void shouldProperlyCreateAccountWithAdministratorAccessLevel() {
			AccountCreateDto account = InitData.getAccountToCreate();
			account.setLogin("Administrator123");
			account.setEmail("administrator123@example.com");
			account.setAccessLevel(new AccessLevelDto("administrator"));
			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/create")
							.then()
							.statusCode(201);

			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.when()
							.get("/account/login/Administrator123")
							.then()
							.statusCode(200)
							.contentType("application/json")
							.body("accountState", equalTo("ACTIVE"))
							.body("roles", hasSize(1))
							.body("roles[0]", equalTo("administrator"));
		}

		@Test
		@Order(5)
		@DisplayName("Should properly create new activated account with sales representative access level")
		void shouldProperlyCreateAccountWithSalesRepAccessLevel() {
			AccountCreateDto account = InitData.getAccountToCreate();
			account.setLogin("SalesRep123");
			account.setEmail("salesrep123@example.com");
			account.setAccessLevel(new AccessLevelDto("sales_rep"));
			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/create")
							.then()
							.statusCode(201);

			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.when()
							.get("/account/login/SalesRep123")
							.then()
							.statusCode(200)
							.contentType("application/json")
							.body("accountState", equalTo("ACTIVE"))
							.body("roles", hasSize(1))
							.body("roles[0]", equalTo("sales_rep"));
		}
	}

	@Nested
	@Order(2)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class Negative {
		@Test
		@Order(1)
		@DisplayName("Should fail to create new account with login that already exists")
		void shouldFailToCreateAccountWithSameLogin() {
			AccountCreateDto account = InitData.getAccountToCreate();
			account.setLogin("Active123");
			account.setEmail("another123@example.com");
			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/create")
							.then()
							.statusCode(409)
							.body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_LOGIN_ALREADY_EXISTS));
		}

		@Test
		@Order(2)
		@DisplayName("Should fail to create new account with email that already exists")
		void shouldFailToCreateAccountWithSameEmail() {
			AccountCreateDto account = InitData.getAccountToCreate();
			account.setLogin("Another123");
			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/create")
							.then()
							.statusCode(409)
							.body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_EMAIL_ALREADY_EXISTS));
		}

		@Test
		@Order(3)
		@DisplayName("Should fail to create new account with company nip that already exists")
		void shouldFailToCreateAccountWithSameCompanyNip() {
			AccountCreateDto account = InitData.getAccountToCreate();
			account.setLogin("Another123");
			account.setEmail("another123@example.com");
			account.setNip("9999999999");
			account.setAccessLevel(new AccessLevelDto("client"));
			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/create")
							.then()
							.statusCode(409)
							.body("message", equalTo(MessageUtil.MessageKey.COMPANY_NIP_ALREADY_EXISTS));
		}

		@Test
		@Order(4)
		@DisplayName("Should fail to create new account with invalid access level")
		void shouldFailToCreateAccountWithInvalidAccessLevel() {
			AccountCreateDto account = InitData.getAccountToCreate();
			account.setAccessLevel(new AccessLevelDto("invalid"));
			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/create")
							.then()
							.statusCode(400)
							.body("message", equalTo(MessageUtil.MessageKey.ACCOUNT_ACCESS_LEVEL));
		}

		@Test
		@Order(5)
		@DisplayName("Should fail to create new account as client")
		void shouldFailToCreateAccountAsClient() {
			AccountCreateDto account = InitData.getAccountToCreate();
			given()
							.header("Authorization", "Bearer " + InitData.retrieveClientToken())
							.contentType("application/json")
							.body(InitData.mapToJsonString(account))
							.when()
							.post("/account/create")
							.then()
							.statusCode(403);
		}
	}
}
