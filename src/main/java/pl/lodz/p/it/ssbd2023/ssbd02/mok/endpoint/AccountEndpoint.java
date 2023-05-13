package pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint;

import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.security.enterprise.AuthenticationException;
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
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.SetEmailToSendPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.AuthenticationService;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.DtoToEntityMapper;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
@Interceptors({LoggerInterceptor.class})
public class AccountEndpoint {

  @Inject
  private AccountService accountService;
  @Inject
  private AuthenticationService authenticationService;


  public void registerAccount(AccountRegisterDto accountRegisterDto) {
    Account account = DtoToEntityMapper.mapAccountRegisterDtoToAccount(accountRegisterDto);
    repeatTransaction(() -> accountService.registerAccount(account));
  }

  public void createAccount(AccountCreateDto accountCreateDto) {
    Account account = DtoToEntityMapper.mapAccountCreateDtoToAccount(accountCreateDto);
    repeatTransaction(() -> accountService.createAccount(account));
  }

  public void blockAccount(Long id) {
    repeatTransaction(() -> accountService.blockAccount(id));
    //TODO email message
  }

  public void activateAccount(Long id) {
    repeatTransaction(() -> accountService.activateAccount(id));
    //TODO email message
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

  public String login(UserCredentialsDto userCredentialsDto, String ip) throws AuthenticationException {
    return authenticationService.login(userCredentialsDto.getLogin(), userCredentialsDto.getPassword(), ip);
  }

  public void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel) {
    repeatTransaction(() -> accountService.addAccessLevelToAccount(accountId, accessLevel));
    Account foundAccount = repeatTransaction(() -> accountService.getAccountById(accountId)).orElseThrow();
    //mailService.sendEmailAboutAddingAccessLevel(foundAccount.getEmail(), foundAccount.getLocale());
  }

  public void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel) {
    repeatTransaction(() -> accountService.removeAccessLevelFromAccount(accountId, accessLevel));
    Account foundAccount = repeatTransaction(() -> accountService.getAccountById(accountId)).orElseThrow();
    //mailService.sendEmailAboutRemovingAccessLevel(foundAccount.getEmail(), foundAccount.getLocale());
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
