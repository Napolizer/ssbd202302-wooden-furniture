package pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
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
import pl.lodz.p.it.ssbd2023.ssbd02.utils.endpoint.AbstractEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.DtoToEntityMapper;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
@Interceptors({LoggerInterceptor.class})
@DenyAll
public class AccountEndpoint extends AbstractEndpoint {

  @Inject
  private AccountService accountService;
  @Inject
  private AuthenticationService authenticationService;


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

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public Optional<Account> getAccountByEmail(SetEmailToSendPasswordDto emailDto) {
    return repeatTransaction(() -> accountService.getAccountByEmail(emailDto.getEmail()));
  }

  @RolesAllowed(ADMINISTRATOR)
  public List<Account> getAccountList() {
    return repeatTransaction(() -> accountService.getAccountList());
  }

  @PermitAll
  public String login(UserCredentialsDto userCredentialsDto, String ip, String locale) throws AuthenticationException {
    return authenticationService.login(userCredentialsDto.getLogin(), userCredentialsDto.getPassword(), ip, locale);
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
  public void changePasswordAsAdmin(String login, String newPassword) {
    repeatTransaction(() -> accountService.changePasswordAsAdmin(login, newPassword));
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
      default -> throw ApplicationExceptionFactory.createInvalidLinkException();
    }
  }

  @PermitAll
  public void resetPassword(String login, ChangePasswordDto changePasswordDto) {
    repeatTransaction(() -> accountService.resetPassword(login, changePasswordDto.getPassword()));
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void sendResetPasswordEmail(SetEmailToSendPasswordDto emailDto) {
    repeatTransaction(() -> accountService.sendResetPasswordEmail(emailDto.getEmail()));
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void updateEmailAfterConfirmation(String login) {
    repeatTransaction(() -> accountService.updateEmailAfterConfirmation(login));
  }

  @RolesAllowed({ADMINISTRATOR, EMPLOYEE, SALES_REP, CLIENT})
  public void changeEmail(SetEmailToSendPasswordDto emailDto, Long accountId, String login) {
    repeatTransaction(() -> accountService.changeEmail(emailDto.getEmail(), accountId, login));
  }

  @Override
  protected boolean isLastTransactionRollback() {
    return accountService.isLastTransactionRollback();
  }
}
