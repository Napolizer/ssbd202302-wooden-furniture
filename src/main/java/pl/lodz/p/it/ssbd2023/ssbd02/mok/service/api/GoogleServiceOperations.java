package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;

@Local
public interface GoogleServiceOperations {

  String getGoogleOauthLink();

  Account getRegisteredAccountOrCreateNew(String code, String state);

  void validateIdToken(String idToken);

  String saveImageInStorage(byte[] image, String fileName);
}
