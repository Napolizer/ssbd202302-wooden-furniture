package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.validation.ValidationException;
import java.util.Date;
import java.util.List;
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
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class TokenService {
  // TODO retrieve this values from environment variable
  private static final String SECRET_KEY = "$3bus>[i-y6e^A<{.:D$WON_*BDz@ooPQ]I~+[Q;UK'+,.-{_o!#~uD$$<i-oR?3e";
  private static final Long EXPIRATION_AUTHORIZATION = 900000L; // 15 minutes
  private static final Long EXPIRATION_ACCOUNT_CONFIRMATION = 86400000L; // 24 hours
  private static final Long EXPIRATION_PASSWORD_RESET = 1200000L; // 20 minutes

  public String generateToken(Account account) {
    long now = System.currentTimeMillis();
    var builder = Jwts.builder()
        .setSubject(account.getLogin())
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + EXPIRATION_AUTHORIZATION))
        .claim("groups", account.getAccessLevels()
            .stream()
            .map(AccessLevel::getGroupName)
            .collect(Collectors.toList()))
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY);
    return builder.compact();
  }

  public TokenClaims getTokenClaims(String token) throws ValidationException {
    try {
      Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();

      String username = claims.getSubject();
      List<String> groups = claims.get("groups", List.class);
      List<AccessLevel> accessLevels = groups
          .stream()
          .map(groupName -> switch (groupName) {
            case "ADMINISTRATORS" -> new Administrator();
            case "EMPLOYEES" -> new Employee();
            case "SALES_REPS" -> new SalesRep();
            case "CLIENTS" -> new Client();
            default -> throw new IllegalStateException("Unexpected value: " + groupName);
          })
          .collect(Collectors.toList());
      return new TokenClaims(username, accessLevels);
    } catch (RuntimeException e) {
      throw new ValidationException(e);
    }
  }

  public String generateTokenForEmailLink(Account account, TokenType tokenType) {
    long now = System.currentTimeMillis();
    String secret = switch (tokenType) {
      case ACCOUNT_CONFIRMATION -> SECRET_KEY;
      case PASSWORD_RESET -> CryptHashUtils.getSecretKeyForPasswordResetToken(account.getPassword());
    };
    Long expiration = switch (tokenType) {
      case ACCOUNT_CONFIRMATION -> EXPIRATION_ACCOUNT_CONFIRMATION;
      case PASSWORD_RESET -> EXPIRATION_PASSWORD_RESET;
    };
    var builder = Jwts.builder()
            .setSubject(account.getLogin())
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + expiration))
            .claim("type", tokenType)
            .signWith(SignatureAlgorithm.HS512, secret);
    return builder.compact();
  }

  public String validateAccountVerificationToken(String token) {
    Claims claims;
    try {
      claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException eje) {
      throw ApplicationExceptionFactory.createExpiredLinkException(eje);
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createInvalidLinkException();
    }
    String type = claims.get("type", String.class);
    if (!type.equals(TokenType.ACCOUNT_CONFIRMATION.name())) {
      throw ApplicationExceptionFactory.createInvalidLinkException();
    }

    return claims.getSubject();
  }
}
