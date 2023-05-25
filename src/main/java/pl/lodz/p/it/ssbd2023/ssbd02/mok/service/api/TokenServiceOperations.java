package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api;

import jakarta.ejb.Local;
import jakarta.validation.ValidationException;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.config.enums.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.security.TokenClaims;

@Local
public interface TokenServiceOperations {

  String generateToken(Account account);

  String generateRefreshToken(Account account);

  void validateRefreshToken(String refreshToken);

  TokenClaims getTokenClaims(String token) throws ValidationException;

  String generateTokenForEmailLink(Account account, TokenType tokenType);

  String validateEmailToken(String token, TokenType tokenType, String key);

  String getLoginFromTokenWithoutValidating(String token, TokenType tokenType);

  String getLoginFromRefreshToken(String refreshToken);

}
