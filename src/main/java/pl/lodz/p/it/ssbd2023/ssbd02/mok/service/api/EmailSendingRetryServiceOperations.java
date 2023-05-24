package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TokenType;

@Local
public interface EmailSendingRetryServiceOperations {

  void sendEmailTokenAfterHalfExpirationTime(String login, String hashOrEmail,
                                                    TokenType tokenType, String token);
}
