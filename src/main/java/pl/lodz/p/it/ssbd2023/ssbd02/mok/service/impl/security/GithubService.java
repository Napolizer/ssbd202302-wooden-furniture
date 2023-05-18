package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.apache.http.client.utils.URIBuilder;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;


@Stateless
public class GithubService {
  @Inject
  private AccountFacadeOperations accountFacade;
  private static final String CLIENT_ID;
  private static final String CLIENT_SECRET;
  private static final String BASE_URI;
  private static final String TOKEN_URI;
  private static final String USER_URI;
  private static final String EMAILS_URI;

  static {
    Properties properties = new Properties();
    try (InputStream inputStream = GithubService.class.getClassLoader().getResourceAsStream("config.properties")) {
      properties.load(inputStream);
      CLIENT_ID = properties.getProperty("github.client.id");
      CLIENT_SECRET = properties.getProperty("github.client.secret");
      BASE_URI = properties.getProperty("github.base.uri");
      TOKEN_URI = properties.getProperty("github.token.uri");
      USER_URI = properties.getProperty("github.user.uri");
      EMAILS_URI = properties.getProperty("github.emails.uri");
    } catch (Exception e) {
      throw new RuntimeException("Error loading configuration file: " + e.getMessage());
    }
  }

  private Account createAccountFromGithubData(String accessToken) {
    return Account.builder()
        .login(retrieveLogin(accessToken))
        .email(retrieveEmail(accessToken))
        .build();
  }

  public String getGithubOauthLink() {
    try {
      URIBuilder uriBuilder = new URIBuilder(BASE_URI);
      uriBuilder.setParameter("client_id", CLIENT_ID);
      return uriBuilder.build().toString();
    } catch (URISyntaxException e) {
      throw ApplicationExceptionFactory.createUnknownErrorException(e);
    }
  }

  private URL getGithubTokenLink(String githubCode) {
    try {
      URIBuilder uriBuilder = new URIBuilder(TOKEN_URI);
      uriBuilder.setParameter("client_id", CLIENT_ID);
      uriBuilder.setParameter("client_secret", CLIENT_SECRET);
      uriBuilder.setParameter("code", githubCode);
      return uriBuilder.build().toURL();
    } catch (URISyntaxException | MalformedURLException e) {
      throw ApplicationExceptionFactory.createUnknownErrorException(e);
    }
  }

  public String retrieveGithubAccessToken(String githubCode) {
    String accessToken = "";

    try {
      URL url = getGithubTokenLink(githubCode);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setRequestProperty("Accept", "application/json");

      int responseCode = connection.getResponseCode();

      if (responseCode == HttpURLConnection.HTTP_OK) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
          StringBuilder responseBody = new StringBuilder();
          String line;
          while ((line = reader.readLine()) != null) {
            responseBody.append(line);
          }

          JsonReader jsonReader = Json.createReader(new StringReader(responseBody.toString()));
          JsonObject jsonResponse = jsonReader.readObject();
          jsonReader.close();

          accessToken = jsonResponse.getString("access_token");
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return accessToken;
  }

  public String retrieveLogin(String accessToken) {
    String login = "";
    try {
      URL url = new URL(USER_URI);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Authorization", "Bearer " + accessToken);
      connection.setRequestProperty("Accept", "application/json");

      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
          StringBuilder responseBody = new StringBuilder();
          String line;
          while ((line = reader.readLine()) != null) {
            responseBody.append(line);
          }

          JsonReader jsonReader = Json.createReader(new StringReader(responseBody.toString()));
          JsonObject jsonResponse = jsonReader.readObject();
          jsonReader.close();

          login = jsonResponse.getString("login");
        }

      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return login;
  }

  public String retrieveEmail(String accessToken) {
    String email = "";
    try {
      URL url = new URL(EMAILS_URI);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Authorization", "Bearer " + accessToken);

      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
          StringBuilder responseBody = new StringBuilder();
          String line;
          while ((line = reader.readLine()) != null) {
            responseBody.append(line);
          }

          JsonReader jsonReader = Json.createReader(new StringReader(responseBody.toString()));
          JsonArray jsonArray = jsonReader.readArray();
          jsonReader.close();

          for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonResponse = jsonArray.getJsonObject(i);
            if (jsonResponse.containsKey("email")) {
              email = jsonResponse.getString("email");
              break;
            }
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return email;
  }

  public Account getRegisteredAccountOrCreateNew(String githubCode) {
    String accessToken = retrieveGithubAccessToken(githubCode);
    String email = retrieveEmail(accessToken);
    Optional<Account> account = accountFacade.findByEmail(email);
    if (account.isEmpty()) {
      return createAccountFromGithubData(accessToken);
    } else if (account.get().getAccountType().equals(AccountType.GITHUB)) {
      return account.get();
    } else {
      throw ApplicationExceptionFactory.createGithubOauthConflictException();
    }
  }
}
