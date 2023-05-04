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
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.security.TokenClaims;


@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class TokenService {
  // TODO retrieve this values from environment variable
  private final String secretKey = "123456789";
  private final String accountConfirmationTokenSecretKey = "123123123";
  private final Long expirationTime = 900000L; // 15 minutes expiration time
  private final Long accountConfirmationTokenExpirationTime = 86400000L; //24 hours

  public String generateToken(Account account) {
    long now = System.currentTimeMillis();
    var builder = Jwts.builder()
        .setSubject(account.getLogin())
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + expirationTime))
        .claim("groups", account.getAccessLevels()
            .stream()
            .map(AccessLevel::getGroupName)
            .collect(Collectors.toList()))
        .signWith(SignatureAlgorithm.HS512, secretKey);
    return builder.compact();
  }

  public TokenClaims getTokenClaims(String token) throws ValidationException {
    try {
      Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

      String username = claims.getSubject();
      List<String> groups = claims.get("groups", List.class);
      List<AccessLevel> accessLevels = groups
          .stream()
          .map(groupName -> switch (groupName) {
            case "ADMINISTRATOR" -> new Administrator();
            case "EMPLOYEE" -> new Employee();
            case "SALES_REP" -> new SalesRep();
            case "CLIENT" -> new Client();
            default -> throw new IllegalStateException("Unexpected value: " + groupName);
          })
          .collect(Collectors.toList());
      return new TokenClaims(username, accessLevels);
    } catch (RuntimeException e) {
      throw new ValidationException(e);
    }
  }

  public String generateAccountVerificationToken(Account account) {
    long now = System.currentTimeMillis();
    var builder = Jwts.builder()
            .setSubject(account.getLogin())
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + accountConfirmationTokenExpirationTime))
            .claim("type", "ACCOUNT_CONFIRMATION")
            .signWith(SignatureAlgorithm.HS512, accountConfirmationTokenSecretKey);
    return builder.compact();
  }

  public String validateAccountVerificationToken(String token) {
    Claims claims;
    try {
      claims = Jwts.parser().setSigningKey(accountConfirmationTokenSecretKey).parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException eje) {
      throw ApplicationExceptionFactory.createExpiredLinkException(eje);
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createInvalidLinkException();
    }
    String type = claims.get("type", String.class);
    if (!type.equals("ACCOUNT_CONFIRMATION")) {
      throw ApplicationExceptionFactory.createInvalidLinkException();
    }

    return claims.getSubject();
  }
}
