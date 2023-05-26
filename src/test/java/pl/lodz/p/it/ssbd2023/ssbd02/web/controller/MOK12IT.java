package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;

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
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AuthUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;
import pl.lodz.p.it.ssbd2023.ssbd02.web.InitData;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@DisplayName("MOK.12 - Show account information")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class MOK12IT {

  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class Init {
    @DisplayName("Should properly create account for first positive test")
    @Test
    @Order(1)
    void shouldProperlyCreateFirstAccountToActivate() {
      given()
              .header("Authorization", "Bearer " + AuthUtil.retrieveToken("administrator", "Student123!"))
              .contentType("application/json")
              .body(InitData.accountToGetInformationsJson)
              .when()
              .post("/account/create")
              .then()
              .statusCode(201);
    }
  }

  @Nested
  @Order(2)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Positive {

    @DisplayName("Should properly get own account information")
    @ParameterizedTest(name = "login: {0}")
    @CsvSource({
            "getInformations",
    })
    @Order(1)
    void shouldProperlyGetOwnAccountInformation(String login) {
      String token = AuthUtil.retrieveToken(login, "Student123!");
      AccountWithoutSensitiveDataDto account = given()
              .header(AUTHORIZATION, "Bearer " + AuthUtil.retrieveToken(ADMINISTRATOR))
              .when()
              .get("/account/login/" + login)
              .then()
              .statusCode(200)
              .extract()
              .body()
              .as(AccountWithoutSensitiveDataDto.class);

      AccountWithoutSensitiveDataDto retrievedAccount = given()
              .header(AUTHORIZATION, "Bearer " + token)
              .when()
              .get("/account/self")
              .then()
              .statusCode(200)
              .extract()
              .body()
              .as(AccountWithoutSensitiveDataDto.class);

      assertEquals(retrievedAccount.getFirstName(), account.getFirstName());
      assertEquals(retrievedAccount.getLastName(), account.getLastName());
      assertEquals(retrievedAccount.getAddress().getCountry(), account.getAddress().getCountry());
      assertEquals(retrievedAccount.getAddress().getCity(), account.getAddress().getCity());
      assertEquals(retrievedAccount.getAddress().getStreet(), account.getAddress().getStreet());
      assertEquals(retrievedAccount.getAddress().getStreetNumber(), account.getAddress().getStreetNumber());
      assertEquals(retrievedAccount.getAddress().getPostalCode(), account.getAddress().getPostalCode());
    }
  }

  @Nested
  @Order(3)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class Negative {

    @Test
    @Order(1)
    @DisplayName("Should fail to get own account information without token")
    void shouldFailToGetOwnAccountInformationWithoutToken() {
      given()
              .when()
              .get("/account/self")
              .then()
              .statusCode(401);
    }

    @Test
    @Order(2)
    @DisplayName("Should fail to get own account information with invalid token")
    void shouldFailToGetOwnAccountInformationWithInvalidToken() {
      String token = "token";
      given()
              .header(AUTHORIZATION, "Bearer " + token)
              .when()
              .get("/account/self")
              .then()
              .statusCode(401);
    }
  }
}
