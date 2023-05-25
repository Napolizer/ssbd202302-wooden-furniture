package pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.json.Json;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountSearchSettings;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Mode;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountSearchSettingsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangeLocaleDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangePasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.FullNameDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.GoogleAccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.SetEmailToSendPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint.api.AccountEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AccountServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AuthenticationServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.GithubServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.GoogleServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.endpoint.AbstractEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.DtoToEntityMapper;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
@Interceptors({LoggerInterceptor.class})
@DenyAll
public class AccountEndpoint extends AbstractEndpoint implements AccountEndpointOperations {

  @Inject
  private AccountServiceOperations accountService;
  @Inject
  private AuthenticationServiceOperations authenticationService;
  @Inject
  private GoogleServiceOperations googleService;
  @Inject
  private GithubServiceOperations githubService;


  @PermitAll
  public void registerAccount(AccountRegisterDto accountRegisterDto) {
    Account account = DtoToEntityMapper.mapAccountRegisterDtoToAccount(accountRegisterDto);
    repeatTransaction(() -> accountService.registerAccount(account));
  }

  @RolesAllowed(ADMINISTRATOR)
  public Account createAccount(AccountCreateDto accountCreateDto) {
    Account account = DtoToEntityMapper.mapAccountCreateDtoToAccount(accountCreateDto);
    return repeatTransaction(() -> accountService.createAccount(account));
  }

  @RolesAllowed(ADMINISTRATOR)
  public void blockAccount(Long id) {
    repeatTransaction(() -> accountService.blockAccount(id));

  }

  @RolesAllowed(ADMINISTRATOR)
  public void activateAccount(Long id) {
    repeatTransaction(() -> accountService.activateAccount(id));
  }

  @RolesAllowed(ADMINISTRATOR)
  public Optional<Account> getAccountByAccountId(Long accountId) {
    return repeatTransaction(() -> accountService.getAccountById(accountId));
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Optional<Account> getAccountByLogin(String login) {
    return repeatTransaction(() -> accountService.getAccountByLogin(login));
  }

  @PermitAll
  public Optional<Account> getAccountByEmail(SetEmailToSendPasswordDto emailDto) {
    return repeatTransaction(() -> accountService.getAccountByEmail(emailDto.getEmail()));
  }

  @RolesAllowed(ADMINISTRATOR)
  public List<Account> getAccountList() {
    return repeatTransaction(() -> accountService.getAccountList());
  }

  @PermitAll
  public List<String> login(UserCredentialsDto userCredentialsDto, String locale)
      throws AuthenticationException {
    return authenticationService.login(userCredentialsDto.getLogin(), userCredentialsDto.getPassword(), locale);
  }

  @RolesAllowed(ADMINISTRATOR)
  public void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel) {
    repeatTransaction(() -> accountService.addAccessLevelToAccount(accountId, accessLevel));
    Account foundAccount = repeatTransaction(() -> accountService.getAccountById(accountId)).orElseThrow();
  }

  @RolesAllowed(ADMINISTRATOR)
  public void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel) {
    repeatTransaction(() -> accountService.removeAccessLevelFromAccount(accountId, accessLevel));
    Account foundAccount = repeatTransaction(() -> accountService.getAccountById(accountId)).orElseThrow();
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Account changePassword(String login, String newPassword, String currentPassword) {
    return repeatTransaction(() -> accountService.changePassword(login, newPassword, currentPassword));
  }

  @RolesAllowed(ADMINISTRATOR)
  public void changePasswordAsAdmin(String login) {
    repeatTransaction(() -> accountService.changePasswordAsAdmin(login));
  }

  @PermitAll
  public Account changePasswordFromLink(String token, String password, String currentPassword) {
    return repeatTransaction(() -> accountService.changePasswordFromLink(token, password, currentPassword));
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void editAccountInfo(String login, EditPersonInfoDto editPersonInfoDto) {
    repeatTransaction(() -> accountService.editAccountInfo(login,
            DtoToEntityMapper.mapEditPersonInfoDtoToAccount(editPersonInfoDto), editPersonInfoDto.getHash()));
  }

  @RolesAllowed(ADMINISTRATOR)
  public void editAccountInfoAsAdmin(String login,
                                     EditPersonInfoDto editPersonInfoDto) {
    repeatTransaction(() -> accountService.editAccountInfoAsAdmin(login,
            DtoToEntityMapper.mapEditPersonInfoDtoToAccount(editPersonInfoDto), editPersonInfoDto.getHash()));
  }

  @RolesAllowed(ADMINISTRATOR)
  public Account changeAccessLevel(Long accountId, AccessLevel accessLevel) {
    return repeatTransaction(() -> accountService.changeAccessLevel(accountId, accessLevel));
  }

  @PermitAll
  public void confirmAccount(String token) {
    repeatTransaction(() -> accountService.confirmAccount(token));
  }

  @PermitAll
  public String validateEmailToken(String token, TokenType tokenType) {
    switch (tokenType) {
      case PASSWORD_RESET -> {
        return repeatTransaction(() -> accountService.validatePasswordResetToken(token));
      }
      case CHANGE_EMAIL -> {
        return repeatTransaction(() -> accountService.validateChangeEmailToken(token));
      }
      case CHANGE_PASSWORD -> {
        return repeatTransaction(() -> accountService.validatePasswordChangeToken(token));
      }
      default -> throw ApplicationExceptionFactory.createInvalidLinkException();
    }
  }

  @PermitAll
  public void resetPassword(String login, ChangePasswordDto changePasswordDto) {
    repeatTransaction(() -> accountService.resetPassword(login, changePasswordDto.getPassword()));
  }

  @PermitAll
  public void sendResetPasswordEmail(SetEmailToSendPasswordDto emailDto) {
    repeatTransaction(() -> accountService.sendResetPasswordEmail(emailDto.getEmail()));
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void updateEmailAfterConfirmation(String login) {
    repeatTransaction(() -> accountService.updateEmailAfterConfirmation(login));
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void changeEmail(SetEmailToSendPasswordDto emailDto, Long accountId, String login, String version) {
    repeatTransaction(() -> accountService.changeEmail(emailDto.getEmail(), accountId, login, version));
  }

  @PermitAll
  public Response handleGoogleRedirect(String code, String state, String ip, String locale) {
    Account account = googleService.getRegisteredAccountOrCreateNew(code, state);

    if (accountService.getAccountByEmail(account.getEmail()).isPresent()) {
      List<String> tokens = authenticationService.loginWithGoogle(account.getEmail(), locale);
      String token = tokens.get(0);
      String refreshToken = tokens.get(1);
      return Response.ok()
          .entity(Json.createObjectBuilder()
              .add("token", token)
              .add("refresh_token", refreshToken)
              .build())
          .build();
    } else {
      return Response.accepted().entity(DtoToEntityMapper.mapAccountToGoogleAccountInfoDto(account)).build();
    }
  }

  @PermitAll
  public String getGoogleOauthLink() {
    return repeatTransaction(() ->  googleService.getGoogleOauthLink());
  }

  @PermitAll
  public Response registerGoogleAccount(GoogleAccountRegisterDto googleAccountRegisterDto, String ip) {
    googleService.validateIdToken(googleAccountRegisterDto.getIdToken());
    Account account = DtoToEntityMapper.mapAccountRegisterDtoToAccount(googleAccountRegisterDto);
    repeatTransaction(() -> accountService.registerGoogleAccount(account));
    List<String> tokens = authenticationService.loginWithGoogle(account.getEmail(), account.getLocale());
    String token = tokens.get(0);
    String refreshToken = tokens.get(1);
    return Response.ok()
        .entity(Json.createObjectBuilder()
            .add("token", token)
            .add("refresh_token", refreshToken)
            .build())
        .build();
  }

  @PermitAll
  public Response handleGithubRedirect(String githubCode, String ip, String locale) {
    Account account = githubService.getRegisteredAccountOrCreateNew(githubCode);

    if (accountService.getAccountByEmail(account.getEmail()).isPresent()) {
      List<String> tokens = authenticationService.loginWithGithub(account.getEmail(), locale);
      String token = tokens.get(0);
      String refreshToken = tokens.get(0);
      return Response.ok()
          .entity(Json.createObjectBuilder()
              .add("token", token)
              .add("refresh_token", refreshToken)
              .build())
          .build();
    } else {
      return Response.accepted().entity(DtoToEntityMapper.mapAccountToGithubAccountInfoDto(account)).build();
    }
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public String generateTokenFromRefresh(String refreshToken) {
    return repeatTransaction(() -> accountService.generateTokenFromRefresh(refreshToken));
  }

  @PermitAll
  public String getGithubOauthLink() {
    return repeatTransaction(() -> githubService.getGithubOauthLink());
  }

  @PermitAll
  public Response registerGithubAccount(AccountRegisterDto githubAccountRegisterDto, String ip) {
    Account account = DtoToEntityMapper.mapAccountRegisterDtoToAccount(githubAccountRegisterDto);
    repeatTransaction(() -> accountService.registerGithubAccount(account));
    List<String> tokens = authenticationService.loginWithGithub(account.getEmail(), account.getLocale());
    String token = tokens.get(0);
    String refreshToken = tokens.get(1);
    return Response.ok()
        .entity(Json.createObjectBuilder()
            .add("token", token)
            .add("refresh_token", refreshToken)
            .build())
        .build();
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public boolean checkIfUserIsForcedToChangePassword(String login) {
    return this.accountService.checkIfUserIsForcedToChangePassword(login);
  }

  @Override
  protected boolean isLastTransactionRollback() {
    return accountService.isLastTransactionRollback();
  }

  @PermitAll
  public void changeLocale(Long accountId, ChangeLocaleDto changeLocaleDto) {
    repeatTransaction(() -> accountService.changeLocale(accountId, changeLocaleDto.getLocale()));
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void changeMode(String login, Mode mode) {
    repeatTransaction(() -> accountService.changeMode(login, mode));
  }

  @RolesAllowed(ADMINISTRATOR)
  public List<Account> findByFullNameLike(String fullName) {
    return repeatTransaction(() -> accountService.findByFullNameLike(fullName));
  }

  @RolesAllowed(ADMINISTRATOR)
  public List<FullNameDto> autoCompleteFullNames(String phrase) {
    List<Account> accounts = repeatTransaction(() -> accountService.findByFullNameLike(phrase));
    List<FullNameDto> fullNameDtos = new ArrayList<>();
    for (Account account : accounts) {
      fullNameDtos.add(DtoToEntityMapper.mapAccountNameToFullNameDto(account));
    }
    return fullNameDtos;
  }

  @RolesAllowed(ADMINISTRATOR)
  public List<Account> findByFullNameLikeWithPagination(String login,
                                                        AccountSearchSettingsDto accountSearchSettingsDto) {
    AccountSearchSettings accountSearchSettings =
        DtoToEntityMapper.mapAccountSearchSettingsDtoToAccountSearchSettings(accountSearchSettingsDto);
    return repeatTransaction(() -> accountService.findByFullNameLikeWithPagination(login, accountSearchSettings));
  }

  @RolesAllowed(ADMINISTRATOR)
  public AccountSearchSettings getAccountSearchSettings(String login) {
    return repeatTransaction(()-> accountService.getAccountSearchSettings(login));
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Mode getAccountMode(String login) {
    return repeatTransaction(()->accountService.getAccountMode(login));
  }
}
