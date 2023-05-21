package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.validation.ValidationException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Administrator;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Client;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Employee;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.SalesRep;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.security.TokenClaims;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@DenyAll
public class TokenService {
  private static final String SECRET_KEY;
  private static final String SECRET_KEY_REFRESH;
  private static final Long EXPIRATION_AUTHORIZATION;
  private static final Long EXPIRATION_AUTHORIZATION_REFRESH;
  private static final Long EXPIRATION_ACCOUNT_CONFIRMATION;
  private static final Long EXPIRATION_PASSWORD_RESET;
  private static final Long EXPIRATION_CHANGE_EMAIL;
  private static Long EXPIRATION_CHANGE_PASSWORD;

  static {
    Properties prop = new Properties();
    try (InputStream input = TokenService.class.getClassLoader().getResourceAsStream("config.properties")) {
      prop.load(input);
      SECRET_KEY = prop.getProperty("secret.key");
      SECRET_KEY_REFRESH = prop.getProperty("secret.key.refresh");
      EXPIRATION_AUTHORIZATION = Long.parseLong(prop.getProperty("expiration.authorization.milliseconds"));
      EXPIRATION_AUTHORIZATION_REFRESH = Long.parseLong(prop
          .getProperty("expiration.authorization.refresh.milliseconds"));
      EXPIRATION_ACCOUNT_CONFIRMATION = Long.parseLong(prop
              .getProperty("expiration.account.confirmation.milliseconds"));
      EXPIRATION_PASSWORD_RESET = Long.parseLong(prop.getProperty("expiration.password.reset.milliseconds"));
      EXPIRATION_CHANGE_EMAIL = Long.parseLong(prop.getProperty("expiration.change.email.milliseconds"));
      EXPIRATION_CHANGE_PASSWORD = Long.parseLong(prop.getProperty("expiration.password.change.milliseconds"));
    } catch (Exception e) {
      throw new RuntimeException("Error loading configuration file: " + e.getMessage());
    }
  }

  @PermitAll
  public String generateToken(Account account) {
    long now = System.currentTimeMillis();
    var builder = Jwts.builder()
        .setSubject(account.getLogin())
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + EXPIRATION_AUTHORIZATION))
        .claim("roles", account.getAccessLevels()
            .stream()
            .map(AccessLevel::getRoleName)
            .toList())
        .signWith(SignatureAlgorithm.HS512, SECRET_KEY);
    return builder.compact();
  }

  @PermitAll
  public String generateRefreshToken(Account account) {
    long now = System.currentTimeMillis();
    var builder = Jwts.builder()
        .setSubject(account.getLogin())
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + EXPIRATION_AUTHORIZATION_REFRESH))
        .signWith(SignatureAlgorithm.HS512, SECRET_KEY_REFRESH);
    return builder.compact();
  }

  @PermitAll
  public void validateRefreshToken(String refreshToken) {
    try {
      Jwts.parser().setSigningKey(SECRET_KEY_REFRESH).parseClaimsJws(refreshToken);
    } catch (ExpiredJwtException e) {
      throw ApplicationExceptionFactory.expiredRefreshTokenException();
    } catch (RuntimeException e) {
      throw ApplicationExceptionFactory.invalidRefreshTokenException();
    }
  }

  @PermitAll
  public TokenClaims getTokenClaims(String token) throws ValidationException {
    try {
      Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();

      String username = claims.getSubject();
      List<String> roles = claims.get("roles", List.class);
      List<AccessLevel> accessLevels = roles
          .stream()
          .map(roleName -> switch (roleName) {
            case ADMINISTRATOR -> new Administrator();
            case EMPLOYEE -> new Employee();
            case SALES_REP -> new SalesRep();
            case CLIENT -> new Client();
            default -> throw new IllegalStateException("Unexpected value: " + roleName);
          })
          .collect(Collectors.toList());
      return new TokenClaims(username, accessLevels);
    } catch (RuntimeException e) {
      throw new ValidationException(e);
    }
  }

  @PermitAll
  public String generateTokenForEmailLink(Account account, TokenType tokenType) {
    long now = System.currentTimeMillis();
    String secret = switch (tokenType) {
      case ACCOUNT_CONFIRMATION -> SECRET_KEY;
      case PASSWORD_RESET, CHANGE_PASSWORD -> CryptHashUtils.getSecretKeyForEmailToken(account.getPassword());
      case CHANGE_EMAIL -> CryptHashUtils.getSecretKeyForEmailToken(account.getNewEmail());
    };
    Long expiration = switch (tokenType) {
      case ACCOUNT_CONFIRMATION -> EXPIRATION_ACCOUNT_CONFIRMATION;
      case PASSWORD_RESET -> EXPIRATION_PASSWORD_RESET;
      case CHANGE_EMAIL -> EXPIRATION_CHANGE_EMAIL;
      case CHANGE_PASSWORD -> EXPIRATION_CHANGE_PASSWORD;
    };
    var builder = Jwts.builder()
            .setSubject(account.getLogin())
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + expiration))
            .claim("type", tokenType)
            .signWith(SignatureAlgorithm.HS512, secret);
    return builder.compact();
  }

  @PermitAll
  public String validateEmailToken(String token, TokenType tokenType, String key) {
    Claims claims;
    try {
      switch (tokenType) {
        case ACCOUNT_CONFIRMATION ->
                claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        case PASSWORD_RESET, CHANGE_PASSWORD, CHANGE_EMAIL ->
                claims = Jwts.parser().setSigningKey(CryptHashUtils.getSecretKeyForEmailToken(key))
                  .parseClaimsJws(token).getBody();
        default -> throw ApplicationExceptionFactory.createInvalidLinkException();
      }
    } catch (ExpiredJwtException eje) {
      switch (tokenType) {
        case ACCOUNT_CONFIRMATION ->
                throw ApplicationExceptionFactory.createAccountConfirmationExpiredLinkException();
        case PASSWORD_RESET ->
                throw ApplicationExceptionFactory.createPasswordResetExpiredLinkException();
        case CHANGE_EMAIL ->
                throw ApplicationExceptionFactory.createChangeEmailExpiredLinkException();
        case CHANGE_PASSWORD ->
                throw ApplicationExceptionFactory.createPasswordResetExpiredLinkException(); //change
        default -> throw ApplicationExceptionFactory.createInvalidLinkException();
      }
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createInvalidLinkException();
    }
    String type = claims.get("type", String.class);
    if (!type.equals(tokenType.name())) {
      throw ApplicationExceptionFactory.createInvalidLinkException();
    }

    return claims.getSubject();
  }

  @PermitAll
  public String getLoginFromTokenWithoutValidating(String token, TokenType tokenType) {
    String claims = token.substring(0, token.lastIndexOf('.') + 1);
    try {
      return Jwts.parser().parseClaimsJwt(claims).getBody().getSubject();
    } catch (ExpiredJwtException eje) {
      switch (tokenType) {
        case PASSWORD_RESET,
                CHANGE_PASSWORD -> throw ApplicationExceptionFactory.createPasswordResetExpiredLinkException();
        case CHANGE_EMAIL -> throw ApplicationExceptionFactory.createChangeEmailExpiredLinkException();
        default -> throw ApplicationExceptionFactory.createUnknownErrorException(eje);
      }
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createInvalidLinkException();
    }
  }
}
