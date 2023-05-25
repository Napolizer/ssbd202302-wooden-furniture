package pl.lodz.p.it.ssbd2023.ssbd02.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.enums.TokenType.PASSWORD_RESET;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import pl.lodz.p.it.ssbd2023.ssbd02.testcontainers.util.AccountUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.web.AppContainerConfig;

@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("MOK.21 - Reset password")
public class MOK21IT {
  @Nested
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  class SendResetPasswordMail {
    @Nested
    @Order(1)
    class Positive {
      @DisplayName("Should properly send reset password email")
      @ParameterizedTest(name = "email: {0}")
      @CsvSource({
          "admin@gmail.com",
          "adam.mickiewicz@gmail.com",
          "juliusz.slowacki@gmail.com",
          "cyprian.norwid@gmail.com"
      })
      void shouldProperlySendResetPasswordEmail(String email) {
        given()
            .contentType("application/json")
            .body("""
                       {
                           "email": "%s"
                       }
                """.formatted(email))
            .when()
            .post("/account/forgot-password")
            .then()
            .statusCode(200)
            .contentType("application/json")
            .body("message", is(equalTo("reset.password.success")));
      }

      @Nested
      @Order(2)
      @TestClassOrder(ClassOrderer.OrderAnnotation.class)
      class Negative {
        @DisplayName("Should fail if email is in invalid format")
        @ParameterizedTest(name = "invalid email: {0}")
        @CsvSource({
            "blabla",
            "123",
            "@",
            "null",
            "abc@",
            "@123"
        })
        @Order(1)
        void shouldFailIfEmailIsInInvalidFormat(String email) {
          given()
              .contentType("application/json")
              .body("""
                         {
                             "email": "%s"
                         }
                  """.formatted(email))
              .when()
              .post("/account/forgot-password")
              .then()
              .statusCode(400)
              .contentType("application/json")
              .body("errors", notNullValue())
              .body("errors.size()", is(equalTo(1)))
              .body("errors[0].message", is(equalTo("must be a well-formed email address")));
        }

        @DisplayName("Should fail if email is empty")
        @Test
        @Order(2)
        void shouldFailIfEmailIsEmpty() {
          given()
              .contentType("application/json")
              .body("""
                         {
                             "email": ""
                         }
                  """)
              .when()
              .post("/account/forgot-password")
              .then()
              .statusCode(400)
              .contentType("application/json")
              .body("errors", notNullValue())
              .body("errors.size()", is(equalTo(1)))
              .body("errors[0].message", is(equalTo("must not be blank")));
        }

        @DisplayName("Should fail to change email in invalid format")
        @ParameterizedTest(name = "invalid email: {0}")
        @CsvSource({
            "does.not.exist@ssbd.com",
            "another.non.existing@ssbd.com"
        })
        @Order(3)
        void shouldFailIfEmailDoesNotExist(String email) {
          given()
              .contentType("application/json")
              .body("""
                         {
                             "email": "%s"
                         }
                  """.formatted(email))
              .when()
              .post("/account/forgot-password")
              .then()
              .statusCode(404)
              .contentType("application/json")
              .body("message", notNullValue())
              .body("message", is(equalTo("exception.mok.account.email.does.not.exist")));
        }

        @DisplayName("Should fail if account is inactive")
        @Test
        @Order(4)
        void shouldFailIfAccountIsInactive() {
          AccountUtil.registerUser("inactiveaccount");

          given()
              .contentType("application/json")
              .body("""
                         {
                             "email": "inactiveaccount@ssbd.com"
                         }
                  """)
              .when()
              .post("/account/forgot-password")
              .then()
              .statusCode(400)
              .contentType("application/json")
              .body("message", notNullValue())
              .body("message", is(equalTo("exception.mok.account.not.active")));
        }
      }
    }

    @Nested
    @Order(2)
    class ResetPassword {
      private String generateResetPasswordToken(boolean expired) {
        long now = System.currentTimeMillis();
        String secret =
            CryptHashUtils.getSecretKeyForEmailToken("$2a$12$X7QVm.XkCx3l97z0/LbxzewopH6ift/IU9kDPfq834MYPfV7w27pe");
        long expiration = 3000000000000L;
        if (expired) {
          expiration = -1L;
        }

        var builder = Jwts.builder()
            .setSubject("client")
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + expiration))
            .claim("type", PASSWORD_RESET)
            .signWith(SignatureAlgorithm.HS512, secret);
        return builder.compact();
      }

      @Nested
      @TestClassOrder(ClassOrderer.OrderAnnotation.class)
      class Negative {
        @DisplayName("Should fail to reset password if token is invalid")
        @ParameterizedTest(name = "invalid token: {0}")
        @CsvSource({
            "wiatrak"
        })
        @Order(1)
        void shouldFailToResetPasswordIfTokenIsInvalid(String invalidToken) {
          given()
              .contentType("application/json")
              .body("""
                         {
                             "currentPassword": "Student123!",
                             "password": "Student321!"
                         }
                  """)
              .when()
              .put("/account/reset-password?token=" + invalidToken)
              .then()
              .statusCode(400)
              .contentType("application/json")
              .body("message", is(equalTo("exception.invalid.link")));
        }

        @DisplayName("Should fail to reset password if token is expired")
        @Test
        @Order(2)
        void shouldFailToResetPasswordIfTokenIsExpired() {
          String expiredToken = generateResetPasswordToken(true);
          given()
              .contentType("application/json")
              .body("""
                      {
                          "currentPassword": "Student123!",
                          "password": "Student321!"
                      }
               """)
              .when()
              .put("/account/reset-password?token=" + expiredToken)
              .then()
              .statusCode(410)
              .contentType("application/json")
              .body("message", is(equalTo("exception.expired.password.reset.link")));
        }

        @DisplayName("Should fail to reset password if current password is invalid")
        @ParameterizedTest(name = "invalid password: {0}")
        @CsvSource({
            "InvalidPassword123",
            "Student",
            "blabla"
        })
        @Order(3)
        void shouldFailToResetPasswordIfCurrentPasswordIsInvalid(String invalidCurrentPassword) {
          String token = generateResetPasswordToken(false);
          given()
              .contentType("application/json")
              .body("""
                         {
                             "currentPassword": "%s",
                             "password": "Student321!"
                         }
                  """.formatted(invalidCurrentPassword))
              .when()
              .put("/account/reset-password?token=" + token)
              .then()
              .statusCode(400)
              .contentType("application/json")
              .body("errors", notNullValue());
        }

        @DisplayName("Should fail to reset password if new password is invalid")
        @ParameterizedTest(name = "invalid new password: {0}")
        @CsvSource({
            "sds",
            "Student",
            "Student123"
        })
        @Order(4)
        void shouldFailToResetPasswordIfNewPasswordIsInvalid(String invalidNewPassword) {
          String token = generateResetPasswordToken(false);
          given()
              .contentType("application/json")
              .body("""
                         {
                             "currentPassword": "Student123!",
                             "password": "%s"
                         }
                  """.formatted(invalidNewPassword))
              .when()
              .put("/account/reset-password?token=" + token)
              .then()
              .statusCode(400)
              .contentType("application/json")
              .body("errors", is(notNullValue()));
        }

        @DisplayName("Should fail to reset password if new password is same as old")
        @Test
        @Order(5)
        void shouldFailToResetPasswordIfNewPasswordIsSameAsOld() {
          String token = generateResetPasswordToken(false);
          given()
              .contentType("application/json")
              .body("""
                         {
                             "currentPassword": "Student123!",
                             "password": "Student123!"
                         }
                  """)
              .when()
              .put("/account/reset-password?token=" + token)
              .then()
              .statusCode(409)
              .contentType("application/json")
              .body("message", is(equalTo("exception.mok.account.old.password")));
        }
      }
    }
  }
}
