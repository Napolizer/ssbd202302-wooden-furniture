package pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint;

import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.json.Json;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangePasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.GoogleAccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.SetEmailToSendPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.AuthenticationService;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.GoogleService;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.DtoToEntityMapper;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
@Interceptors({LoggerInterceptor.class})
public class AccountEndpoint {

  @Inject
  private AccountService accountService;
  @Inject
  private GoogleService googleService;
  @Inject
  private AuthenticationService authenticationService;


  public void registerAccount(AccountRegisterDto accountRegisterDto) {
    Account account = DtoToEntityMapper.mapAccountRegisterDtoToAccount(accountRegisterDto);
    repeatTransaction(() -> accountService.registerAccount(account));
  }

  public Account createAccount(AccountCreateDto accountCreateDto) {
    Account account = DtoToEntityMapper.mapAccountCreateDtoToAccount(accountCreateDto);
    return repeatTransaction(() -> accountService.createAccount(account));
  }

  public void blockAccount(Long id) {
    repeatTransaction(() -> accountService.blockAccount(id));

  }

  public void activateAccount(Long id) {
    repeatTransaction(() -> accountService.activateAccount(id));
  }

  public Optional<Account> getAccountByAccountId(Long accountId) {
    return repeatTransaction(() -> accountService.getAccountById(accountId));
  }

  public Optional<Account> getAccountByLogin(String login) {
    return repeatTransaction(() -> accountService.getAccountByLogin(login));
  }

  public Optional<Account> getAccountByEmail(SetEmailToSendPasswordDto emailDto) {
    return repeatTransaction(() -> accountService.getAccountByEmail(emailDto.getEmail()));
  }

  public List<Account> getAccountList() {
    return repeatTransaction(() -> accountService.getAccountList());
  }

  public String login(UserCredentialsDto userCredentialsDto, String ip, String locale) throws AuthenticationException {
    return authenticationService.login(userCredentialsDto.getLogin(), userCredentialsDto.getPassword(), ip, locale);
  }

  public void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel) {
    repeatTransaction(() -> accountService.addAccessLevelToAccount(accountId, accessLevel));
    Account foundAccount = repeatTransaction(() -> accountService.getAccountById(accountId)).orElseThrow();
  }

  public void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel) {
    repeatTransaction(() -> accountService.removeAccessLevelFromAccount(accountId, accessLevel));
    Account foundAccount = repeatTransaction(() -> accountService.getAccountById(accountId)).orElseThrow();
  }

  public Account changePassword(String login, String newPassword, String currentPassword) {
    return repeatTransaction(() -> accountService.changePassword(login, newPassword, currentPassword));
  }

  public void changePasswordAsAdmin(String login, String newPassword) {
    repeatTransaction(() -> accountService.changePasswordAsAdmin(login, newPassword));
  }

  public void editAccountInfo(String login, EditPersonInfoDto editPersonInfoDto) {
    repeatTransaction(() -> accountService.editAccountInfo(login,
            DtoToEntityMapper.mapEditPersonInfoDtoToAccount(editPersonInfoDto), editPersonInfoDto.getHash()));
  }

  public void editAccountInfoAsAdmin(String login,
                                     EditPersonInfoDto editPersonInfoDto) {
    repeatTransaction(() -> accountService.editAccountInfoAsAdmin(login,
            DtoToEntityMapper.mapEditPersonInfoDtoToAccount(editPersonInfoDto), editPersonInfoDto.getHash()));
  }

  public Account changeAccessLevel(Long accountId, AccessLevel accessLevel) {
    return repeatTransaction(() -> accountService.changeAccessLevel(accountId, accessLevel));
  }

  public void confirmAccount(String token) {
    repeatTransaction(() -> accountService.confirmAccount(token));
  }

  public String validateEmailToken(String token, TokenType tokenType) {
    switch (tokenType) {
      case PASSWORD_RESET -> {
        return repeatTransaction(() -> accountService.validatePasswordResetToken(token));
      }
      case CHANGE_EMAIL -> {
        return repeatTransaction(() -> accountService.validateChangeEmailToken(token));
      }
      default -> throw ApplicationExceptionFactory.createInvalidLinkException();
    }
  }

  public void resetPassword(String login, ChangePasswordDto changePasswordDto) {
    repeatTransaction(() -> accountService.resetPassword(login, changePasswordDto.getPassword()));
  }

  public void sendResetPasswordEmail(SetEmailToSendPasswordDto emailDto) {
    repeatTransaction(() -> accountService.sendResetPasswordEmail(emailDto.getEmail()));
  }

  public void updateEmailAfterConfirmation(String login) {
    repeatTransaction(() -> accountService.updateEmailAfterConfirmation(login));
  }

  public void changeEmail(SetEmailToSendPasswordDto emailDto, Long accountId, String login) {
    repeatTransaction(() -> accountService.changeEmail(emailDto.getEmail(), accountId, login));
  }

  public Response handleGoogleRedirect(String code, String state, String ip, String locale) {
    Account account = googleService.getRegisteredAccountOrCreateNew(code, state);

    if (accountService.getAccountByEmail(account.getEmail()).isPresent()) {
      String token = authenticationService.loginWithGoogle(account.getEmail(), ip, locale);
      return Response.ok().entity(Json.createObjectBuilder().add("token", token).build()).build();
    } else {
      return Response.accepted().entity(DtoToEntityMapper.mapAccountToGoogleAccountInfoDto(account)).build();
    }
  }

  public String getGoogleOauthLink() {
    return repeatTransaction(() ->  googleService.getGoogleOauthLink());
  }

  public Response registerGoogleAccount(GoogleAccountRegisterDto googleAccountRegisterDto, String ip) {
    googleService.validateIdToken(googleAccountRegisterDto.getIdToken());
    Account account = DtoToEntityMapper.mapAccountRegisterDtoToAccount(googleAccountRegisterDto);
    repeatTransaction(() -> accountService.registerGoogleAccount(account));
    String token = authenticationService.loginWithGoogle(account.getEmail(), ip, account.getLocale());
    return Response.ok().entity(Json.createObjectBuilder().add("token", token).build()).build();
  }

  private <T> T repeatTransaction(TransactionMethod<T> method) {
    int retryCounter = 3;
    boolean isRollback;
    T result = null;

    do {
      try {
        result = method.run();
        isRollback = accountService.isLastTransactionRollback();
      } catch (EJBTransactionRolledbackException ex) {
        isRollback = true;
      }

    } while (isRollback && --retryCounter > 0);

    if (isRollback && retryCounter == 0) {
      throw new EJBTransactionRolledbackException();
    } else {
      return result;
    }
  }

  private void repeatTransaction(VoidTransactionMethod method) {
    int retryCounter = 3;
    boolean isRollback;

    do {
      try {
        method.run();
        isRollback = accountService.isLastTransactionRollback();
      } catch (EJBTransactionRolledbackException ex) {
        isRollback = true;
      }

    } while (isRollback && --retryCounter > 0);

    if (isRollback && retryCounter == 0) {
      throw new EJBTransactionRolledbackException();
    }
  }

  private interface TransactionMethod<T> {
    T run();
  }

  private interface VoidTransactionMethod {
    void run();
  }
}
