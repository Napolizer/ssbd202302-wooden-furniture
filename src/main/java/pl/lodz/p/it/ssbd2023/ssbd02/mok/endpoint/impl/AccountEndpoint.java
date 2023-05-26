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
import java.util.stream.Collectors;
import pl.lodz.p.it.ssbd2023.ssbd02.config.enums.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountSearchSettings;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Mode;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountSearchSettingsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangeLocaleDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangePasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.FullNameDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.GoogleAccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.SetEmailToSendPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper.AccountMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper.DtoToEntityMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint.api.AccountEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AccountServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AuthenticationServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.GithubServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.GoogleServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericEndpointExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.endpoint.AbstractEndpoint;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
@Interceptors({
    GenericEndpointExceptionsInterceptor.class,
    LoggerInterceptor.class
})
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

  @Inject
  private AccountMapper accountMapper;


  @PermitAll
  public void registerAccount(AccountRegisterDto accountRegisterDto) {
    Account account = DtoToEntityMapper.mapAccountRegisterDtoToAccount(accountRegisterDto);
    repeatTransactionWithOptimistic(() -> accountService.registerAccount(account));
  }

  @RolesAllowed(ADMINISTRATOR)
  public AccountWithoutSensitiveDataDto createAccount(AccountCreateDto accountCreateDto) {
    Account account = DtoToEntityMapper.mapAccountCreateDtoToAccount(accountCreateDto);
    Account created = repeatTransactionWithOptimistic(() -> accountService.createAccount(account));
    return accountMapper.mapToAccountWithoutSensitiveDataDto(created);
  }

  @RolesAllowed(ADMINISTRATOR)
  public Account blockAccount(Long id) {
    return repeatTransactionWithOptimistic(() -> accountService.blockAccount(id));
  }

  @RolesAllowed(ADMINISTRATOR)
  public Account activateAccount(Long id) {
    return repeatTransactionWithOptimistic(() -> accountService.activateAccount(id));
  }

  @RolesAllowed(ADMINISTRATOR)
  public AccountWithoutSensitiveDataDto getAccountByAccountId(Long accountId) {
    return repeatTransactionWithoutOptimistic(() -> accountService.getAccountById(accountId))
            .map(accountMapper::mapToAccountWithoutSensitiveDataWithTimezone)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public AccountWithoutSensitiveDataDto getAccountByLogin(String login) {
    return repeatTransactionWithoutOptimistic(() -> accountService.getAccountByLogin(login))
            .map(accountMapper::mapToAccountWithoutSensitiveDataWithTimezone)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);
  }

  @PermitAll
  public Account getAccountByEmail(SetEmailToSendPasswordDto emailDto) {
    return repeatTransactionWithoutOptimistic(() -> accountService.getAccountByEmail(emailDto.getEmail()))
            .orElseThrow(ApplicationExceptionFactory::createEmailNotFoundException);
  }

  @RolesAllowed(ADMINISTRATOR)
  public List<AccountWithoutSensitiveDataDto> getAccountList() {
    return repeatTransactionWithoutOptimistic(() -> accountService.getAccountList()).stream()
            .map(accountMapper::mapToAccountWithoutSensitiveDataDto)
            .collect(Collectors.toList());
  }

  @PermitAll
  public List<String> login(UserCredentialsDto userCredentialsDto, String locale)
      throws AuthenticationException {
    return authenticationService.login(userCredentialsDto.getLogin(), userCredentialsDto.getPassword(), locale);
  }

  @RolesAllowed(ADMINISTRATOR)
  public AccountWithoutSensitiveDataDto addAccessLevelToAccount(Long accountId, String accessLevel) {
    AccessLevel newAccessLevel = DtoToEntityMapper
            .mapAccessLevelDtoToAccessLevel(new AccessLevelDto(accessLevel));
    Account account = repeatTransactionWithOptimistic(
            () -> accountService.addAccessLevelToAccount(accountId, newAccessLevel)
    );
    return accountMapper.mapToAccountWithoutSensitiveDataDto(account);
  }

  @RolesAllowed(ADMINISTRATOR)
  public AccountWithoutSensitiveDataDto removeAccessLevelFromAccount(Long accountId, String accessLevel) {
    AccessLevel newAccessLevel = DtoToEntityMapper
            .mapAccessLevelDtoToAccessLevel(new AccessLevelDto(accessLevel));
    Account account = repeatTransactionWithOptimistic(
            () -> accountService.removeAccessLevelFromAccount(accountId, newAccessLevel)
    );
    return accountMapper.mapToAccountWithoutSensitiveDataDto(account);
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public AccountWithoutSensitiveDataDto changePassword(String login, String newPassword, String currentPassword) {
    Account account = repeatTransactionWithOptimistic(
            () -> accountService.changePassword(login, newPassword, currentPassword)
    );
    return accountMapper.mapToAccountWithoutSensitiveDataDto(account);
  }

  @RolesAllowed(ADMINISTRATOR)
  public void changePasswordAsAdmin(String login) {
    repeatTransactionWithOptimistic(() -> accountService.changePasswordAsAdmin(login));
  }

  @PermitAll
  public AccountWithoutSensitiveDataDto changePasswordFromLink(String token, String password, String currentPassword) {
    Account account = repeatTransactionWithOptimistic(
            () -> accountService.changePasswordFromLink(token, password, currentPassword)
    );
    return accountMapper.mapToAccountWithoutSensitiveDataDto(account);
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public AccountWithoutSensitiveDataDto editAccountInfo(String login, EditPersonInfoDto editPersonInfoDto) {
    Account account = repeatTransactionWithoutOptimistic(() -> accountService.editAccountInfo(login,
            DtoToEntityMapper.mapEditPersonInfoDtoToAccount(editPersonInfoDto), editPersonInfoDto.getHash()));
    return accountMapper.mapToAccountWithoutSensitiveDataDto(account);
  }

  @RolesAllowed(ADMINISTRATOR)
  public AccountWithoutSensitiveDataDto editAccountInfoAsAdmin(String login,
                                     EditPersonInfoDto editPersonInfoDto) {
    Account account = repeatTransactionWithoutOptimistic(() -> accountService.editAccountInfoAsAdmin(login,
            DtoToEntityMapper.mapEditPersonInfoDtoToAccount(editPersonInfoDto), editPersonInfoDto.getHash()));
    return accountMapper.mapToAccountWithoutSensitiveDataDto(account);
  }

  @RolesAllowed(ADMINISTRATOR)
  public AccountWithoutSensitiveDataDto changeAccessLevel(Long accountId, AccessLevelDto accessLevel) {
    AccessLevel newAccessLevel = DtoToEntityMapper
            .mapAccessLevelDtoToAccessLevel(accessLevel);
    Account updatedAccount = repeatTransactionWithOptimistic(
            () -> accountService.changeAccessLevel(accountId, newAccessLevel)
    );
    return accountMapper.mapToAccountWithoutSensitiveDataDto(updatedAccount);
  }

  @PermitAll
  public void confirmAccount(String token) {
    repeatTransactionWithOptimistic(() -> accountService.confirmAccount(token));
  }

  @PermitAll
  public String validateEmailToken(String token, TokenType tokenType) {
    switch (tokenType) {
      case PASSWORD_RESET -> {
        return repeatTransactionWithOptimistic(() -> accountService.validatePasswordResetToken(token));
      }
      case CHANGE_EMAIL -> {
        return repeatTransactionWithOptimistic(() -> accountService.validateChangeEmailToken(token));
      }
      case CHANGE_PASSWORD -> {
        return repeatTransactionWithOptimistic(() -> accountService.validatePasswordChangeToken(token));
      }
      default -> throw ApplicationExceptionFactory.createInvalidLinkException();
    }
  }

  @PermitAll
  public void resetPassword(String token, ChangePasswordDto changePasswordDto) {
    repeatTransactionWithOptimistic(() -> accountService.resetPassword(token, changePasswordDto.getPassword()));
  }

  @PermitAll
  public void sendResetPasswordEmail(SetEmailToSendPasswordDto emailDto) {
    repeatTransactionWithOptimistic(() -> accountService.sendResetPasswordEmail(emailDto.getEmail()));
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void updateEmailAfterConfirmation(String token) {
    repeatTransactionWithoutOptimistic(() -> accountService.updateEmailAfterConfirmation(token));
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void changeEmail(SetEmailToSendPasswordDto emailDto, Long accountId, String login, String version) {
    repeatTransactionWithoutOptimistic(
            () -> accountService.changeEmail(emailDto.getEmail(), accountId, login, version)
    );
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
    return repeatTransactionWithOptimistic(() ->  googleService.getGoogleOauthLink());
  }

  @PermitAll
  public Response registerGoogleAccount(GoogleAccountRegisterDto googleAccountRegisterDto, String ip) {
    googleService.validateIdToken(googleAccountRegisterDto.getIdToken());
    Account account = DtoToEntityMapper.mapAccountRegisterDtoToAccount(googleAccountRegisterDto);
    repeatTransactionWithoutOptimistic(() -> accountService.registerGoogleAccount(account));
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
    return repeatTransactionWithOptimistic(() -> accountService.generateTokenFromRefresh(refreshToken));
  }

  @PermitAll
  public String getGithubOauthLink() {
    return repeatTransactionWithOptimistic(() -> githubService.getGithubOauthLink());
  }

  @PermitAll
  public Response registerGithubAccount(AccountRegisterDto githubAccountRegisterDto, String ip) {
    Account account = DtoToEntityMapper.mapAccountRegisterDtoToAccount(githubAccountRegisterDto);
    repeatTransactionWithOptimistic(() -> accountService.registerGithubAccount(account));
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
    repeatTransactionWithoutOptimistic(() -> accountService.changeLocale(accountId, changeLocaleDto.getLocale()));
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void changeMode(String login, Mode mode) {
    repeatTransactionWithOptimistic(() -> accountService.changeMode(login, mode));
  }

  @RolesAllowed(ADMINISTRATOR)
  public List<AccountWithoutSensitiveDataDto> findByFullNameLike(String fullName) {
    return repeatTransactionWithoutOptimistic(() -> accountService.findByFullNameLike(fullName)).stream()
            .map(accountMapper::mapToAccountWithoutSensitiveDataDto).toList();
  }

  @RolesAllowed(ADMINISTRATOR)
  public List<FullNameDto> autoCompleteFullNames(String phrase) {
    List<Account> accounts = repeatTransactionWithoutOptimistic(() -> accountService.findByFullNameLike(phrase));
    List<FullNameDto> fullNameDtos = new ArrayList<>();
    for (Account account : accounts) {
      fullNameDtos.add(DtoToEntityMapper.mapAccountNameToFullNameDto(account));
    }
    return fullNameDtos;
  }

  @RolesAllowed(ADMINISTRATOR)
  public List<AccountWithoutSensitiveDataDto> findByFullNameLikeWithPagination(String login,
                                                        AccountSearchSettingsDto accountSearchSettingsDto) {
    AccountSearchSettings accountSearchSettings =
        DtoToEntityMapper.mapAccountSearchSettingsDtoToAccountSearchSettings(accountSearchSettingsDto);
    return repeatTransactionWithoutOptimistic(
            () -> accountService.findByFullNameLikeWithPagination(login, accountSearchSettings)).stream()
            .map(accountMapper::mapToAccountWithoutSensitiveDataDto).toList();
  }

  @RolesAllowed(ADMINISTRATOR)
  public AccountSearchSettingsDto getAccountSearchSettings(String login) {
    AccountSearchSettings accountSearchSettings =
            repeatTransactionWithoutOptimistic(() -> accountService.getAccountSearchSettings(login));
    return DtoToEntityMapper.mapToAccountSearchSettingsDto(accountSearchSettings);
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Mode getAccountMode(String login) {
    return repeatTransactionWithoutOptimistic(() -> accountService.getAccountMode(login));
  }
}
