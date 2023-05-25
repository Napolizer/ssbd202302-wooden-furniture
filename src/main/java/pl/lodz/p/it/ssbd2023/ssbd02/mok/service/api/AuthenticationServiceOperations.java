package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api;

import jakarta.ejb.Local;
import jakarta.security.enterprise.AuthenticationException;
import java.util.List;

@Local
public interface AuthenticationServiceOperations {

  List<String> login(String login, String password, String locale) throws AuthenticationException;

  List<String> loginWithGoogle(String email, String locale);

  List<String> loginWithGithub(String email, String locale);

}
