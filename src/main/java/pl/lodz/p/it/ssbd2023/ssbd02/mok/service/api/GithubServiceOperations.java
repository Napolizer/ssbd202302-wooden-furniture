package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;

@Local
public interface GithubServiceOperations {

  String getGithubOauthLink();

  String retrieveGithubAccessToken(String githubCode);

  String retrieveLogin(String accessToken);

  String retrieveEmail(String accessToken);

  Account getRegisteredAccountOrCreateNew(String githubCode);
}
