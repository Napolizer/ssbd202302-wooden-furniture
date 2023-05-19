package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountType;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.BaseWebApplicationException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;

@Stateless
public class GoogleService {
  @Inject
  private AccountFacadeOperations accountFacade;
  private static final String BASE_URI;
  private static final String TOKEN_URI;
  private static final String VALIDATE_URI;
  private static final String CLIENT_ID;
  private static final String REDIRECT_URI;
  private static final String SCOPE;
  private static final String PROMPT;
  private static final String SECRET_KEY;
  private static final String STATE;
  private static final String GRANT_TYPE;
  private static final String RESPONSE_TYPE;

  static {
    Properties prop = new Properties();
    try (InputStream input = GoogleService.class.getClassLoader().getResourceAsStream("config.properties")) {
      prop.load(input);
      BASE_URI = prop.getProperty("google.base.uri");
      TOKEN_URI = prop.getProperty("google.token.uri");
      VALIDATE_URI = prop.getProperty("google.validate.uri");
      CLIENT_ID = prop.getProperty("google.client.id");
      REDIRECT_URI = prop.getProperty("google.redirect.uri");
      SCOPE = prop.getProperty("google.scope");
      PROMPT = prop.getProperty("google.prompt");
      SECRET_KEY = prop.getProperty("google.secret.key");
      STATE = prop.getProperty("google.state");
      GRANT_TYPE = prop.getProperty("google.grant.type");
      RESPONSE_TYPE = prop.getProperty("google.response.type");
    } catch (Exception e) {
      throw new RuntimeException("Error loading configuration file: " + e.getMessage());
    }
  }

  public String getGoogleOauthLink() {
    try {
      URIBuilder uriBuilder = new URIBuilder(BASE_URI);
      uriBuilder.setParameter("client_id", CLIENT_ID);
      uriBuilder.setParameter("response_type", RESPONSE_TYPE);
      uriBuilder.setParameter("scope", SCOPE);
      uriBuilder.setParameter("redirect_uri", REDIRECT_URI);
      uriBuilder.setParameter("state", STATE);
      uriBuilder.setParameter("prompt", PROMPT);
      return uriBuilder.build().toString();
    } catch (URISyntaxException e) {
      throw ApplicationExceptionFactory.createUnknownErrorException(e);
    }
  }

  public Account getRegisteredAccountOrCreateNew(String code, String state) {
    String token = getIdToken(code, state);
    String email = getAccountEmailFromToken(token);
    Optional<Account> account = accountFacade.findByEmail(email);
    if (account.isEmpty()) {
      return createAccountFromIdTokenClaims(token);
    } else if (account.get().getAccountType().equals(AccountType.GOOGLE)) {
      return account.get();
    } else {
      throw ApplicationExceptionFactory.createGoogleOauthConflictException();
    }
  }

  private String getIdToken(String code, String state) {
    if (!state.equals(STATE)) {
      throw ApplicationExceptionFactory.createInvalidLinkException();
    }
    HttpPost httpPost = new HttpPost(TOKEN_URI);
    final List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("code", code));
    params.add(new BasicNameValuePair("client_id", CLIENT_ID));
    params.add(new BasicNameValuePair("client_secret", SECRET_KEY));
    params.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
    params.add(new BasicNameValuePair("grant_type", GRANT_TYPE));
    try {
      httpPost.setEntity(new UrlEncodedFormEntity(params));
    } catch (UnsupportedEncodingException e) {
      throw ApplicationExceptionFactory.createUnknownErrorException(e);
    }
    try (CloseableHttpClient client = HttpClients.createDefault();
         CloseableHttpResponse response = client.execute(httpPost)) {

      if (response.getStatusLine().getStatusCode() == Response.Status.OK.getStatusCode()) {
        String responseText = EntityUtils.toString(response.getEntity());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseText);
        return jsonNode.get("id_token").asText();
      } else {
        throw ApplicationExceptionFactory.createInvalidLinkException();
      }
    } catch (BaseWebApplicationException be) {
      throw be;
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createUnknownErrorException(e);
    }
  }

  private String getAccountEmailFromToken(String token) {
    String claims = token.substring(0, token.lastIndexOf('.') + 1);
    try {
      return Jwts.parser().parseClaimsJwt(claims).getBody().get("email", String.class);
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createInvalidLinkException();
    }
  }

  private Account createAccountFromIdTokenClaims(String token) {
    String claims = token.substring(0, token.lastIndexOf('.') + 1);
    try {
      Claims tokenBody = Jwts.parser().parseClaimsJwt(claims).getBody();
      return Account.builder()
              .email(tokenBody.get("email", String.class))
              .locale(tokenBody.get("locale", String.class))
              .password(token)
              .person(Person.builder().firstName(tokenBody.get("given_name", String.class))
                      .lastName(tokenBody.get("family_name", String.class))
                      .build())
              .build();
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createInvalidLinkException();
    }
  }

  public void validateIdToken(String idToken) {
    URI uri;
    try {
      URIBuilder uriBuilder = new URIBuilder(VALIDATE_URI);
      uriBuilder.setParameter("id_token", idToken);
      uri = uriBuilder.build();
    } catch (URISyntaxException e) {
      throw ApplicationExceptionFactory.createUnknownErrorException(e);
    }
    HttpGet httpGet = new HttpGet(uri);
    try (CloseableHttpClient client = HttpClients.createDefault();
         CloseableHttpResponse response = client.execute(httpGet)) {

      if (response.getStatusLine().getStatusCode() != Response.Status.OK.getStatusCode()) {
        throw ApplicationExceptionFactory.createInvalidLinkException();
      }
    } catch (BaseWebApplicationException be) {
      throw be;
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createUnknownErrorException(e);
    }
  }
}