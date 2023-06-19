package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.SetEmailToSendPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

//@MicroShedTest
//@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.27 - Change email address(activate new email from link)")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK27IT {
	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = "http://localhost:8080/api/v1";
	}

	@Nested
	@Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class Positive {
		@Test
		@DisplayName("Should properly activate new email with email token")
		@Order(1)
		void shouldProperlyActivateNewEmail() {
			String login = "clientemployee";
			String newEmail = "newemail123@example.com";
			SetEmailToSendPasswordDto emailDto = InitData.getEmailToChange(newEmail);
			String version = InitData.retrieveVersion(login);

			given()
							.contentType("application/json")
							.header(HttpHeaders.AUTHORIZATION, "Bearer " + InitData.retrieveAdminToken())
							.header(HttpHeaders.IF_MATCH, version)
							.body(InitData.mapToJsonString(emailDto))
							.when()
							.put("/account/change-email/" + InitData.retrieveAccountId(login))
							.then()
							.statusCode(200);

			String token = InitData.generateChangeEmailToken(login, newEmail);

			given()
							.contentType("application/json")
							.header(HttpHeaders.AUTHORIZATION, "Bearer " + InitData.retrieveAdminToken())
							.body(InitData.mapToJsonString(emailDto))
							.when()
							.patch("/account/change-email?token=" + token)
							.then()
							.statusCode(200);

			given()
							.header("Authorization", "Bearer " + InitData.retrieveAdminToken())
							.when()
							.get("/account/login/" + login)
							.then()
							.statusCode(200)
							.contentType("application/json")
							.body("email", equalTo(newEmail));
		}
	}

	@Nested
	@Order(2)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	class Negative {
		@Test
		@DisplayName("Should fail to change email with manipulated email token")
		@Order(1)
		void shouldFailToChangeEmailWithManipulatedToken() {
			String login = "clientemployee";
			String newEmail = "anotheremail123@example.com";
			SetEmailToSendPasswordDto emailDto = InitData.getEmailToChange(newEmail);
			String version = InitData.retrieveVersion(login);

			given()
							.contentType("application/json")
							.header(HttpHeaders.AUTHORIZATION, "Bearer " + InitData.retrieveAdminToken())
							.header(HttpHeaders.IF_MATCH, version)
							.body(InitData.mapToJsonString(emailDto))
							.when()
							.put("/account/change-email/" + InitData.retrieveAccountId(login))
							.then()
							.statusCode(200);

			String token = InitData.generateChangeEmailToken(login, newEmail) + "change";

			given()
							.contentType("application/json")
							.header(HttpHeaders.AUTHORIZATION, "Bearer " + InitData.retrieveAdminToken())
							.body(InitData.mapToJsonString(emailDto))
							.when()
							.patch("/account/change-email?token=" + token)
							.then()
							.statusCode(400)
							.body("message", equalTo(MessageUtil.MessageKey.ERROR_INVALID_LINK));
		}

		@Test
		@DisplayName("Should fail to change email with expired email token")
		@Order(2)
		void shouldFailToChangeEmailForAnotherUser() {
			String login = "clientemployee";
			String newEmail = "anotheremail123@example.com";
			SetEmailToSendPasswordDto emailDto = InitData.getEmailToChange(newEmail);
			String version = InitData.retrieveVersion(login);

			given()
							.contentType("application/json")
							.header(HttpHeaders.AUTHORIZATION, "Bearer " + InitData.retrieveAdminToken())
							.header(HttpHeaders.IF_MATCH, version)
							.body(InitData.mapToJsonString(emailDto))
							.when()
							.put("/account/change-email/" + InitData.retrieveAccountId(login))
							.then()
							.statusCode(200);

			String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJLdWJhMTIzIiwiaWF0IjoxN" +
							"jg0NjY3NTU2LCJleHAiOjE2ODQ3NTM5NTYsInR5cGUiOiJBQ0NPVU5UX0NPTkZJUk" +
							"1BVElPTiJ9.YGfR9iIC54jknWOyZCZ5ryg9WVY4B-qyuz6TW-7ujWzsu00a-YzMHcGu2udx-6kOKXttSDXk6QHaDEST1yaZpw";

			given()
							.contentType("application/json")
							.header(HttpHeaders.AUTHORIZATION, "Bearer " + InitData.retrieveAdminToken())
							.body(InitData.mapToJsonString(emailDto))
							.when()
							.patch( "/account/change-email?token=" + token)
							.then()
							.statusCode(410)
							.body("message", equalTo(MessageUtil.MessageKey.ERROR_EXPIRED_CHANGE_EMAIL_LINK));
		}
	}
}
