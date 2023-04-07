package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.security.TokenClaims;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class TokenService {
    // TODO retrieve this values from environment variable
    private String secretKey = "123456789";
    private Long expirationTime = 900000L; // 15 minutes expiration time

    public String generateToken(Account account) {
        long now = System.currentTimeMillis();
        var builder = Jwts.builder()
                .setSubject(account.getLogin())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationTime))
                .claim("groups", account.getAccessLevels()
                        .stream()
                        .map(accessLevel -> accessLevel.getClass().getSimpleName())
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
                    .map(accessLevelName -> switch (accessLevelName) {
                        case "Administrator" -> new Administrator();
                        case "Client" -> new Client();
                        case "Employee" -> new Employee();
                        case "SalesRep" -> new SalesRep();
                        default -> throw new IllegalStateException("Unexpected value: " + accessLevelName);
                    })
                    .collect(Collectors.toList());
            return new TokenClaims(username, accessLevels);
        } catch (RuntimeException e) {
            throw new ValidationException(e);
        }
    }
}
