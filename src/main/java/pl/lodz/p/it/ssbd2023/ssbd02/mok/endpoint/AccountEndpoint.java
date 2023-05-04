package pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangePasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoAsAdminDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.SetEmailToSendPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security.AuthenticationService;
import pl.lodz.p.it.ssbd2023.ssbd02.web.mappers.DtoToEntityMapper;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
public class AccountEndpoint {

  @Inject
  private AccountService accountService;
  @Inject
  private AuthenticationService authenticationService;

  public void registerAccount(AccountRegisterDto accountRegisterDto) {
    Account account = DtoToEntityMapper.mapAccountRegisterDtoToAccount(accountRegisterDto);
    accountService.registerAccount(account);
  }

  public void createAccount(AccountCreateDto accountCreateDto) {
    Account account = DtoToEntityMapper.mapAccountCreateDtoToAccount(accountCreateDto);
    accountService.createAccount(account);
  }

  public void blockAccount(Long id) {
    accountService.blockAccount(id);
    //TODO email message
  }

  public void activateAccount(Long id) {
    accountService.activateAccount(id);
    //TODO email message
  }

  public Optional<Account> getAccountByAccountId(Long accountId) {
    return accountService.getAccountById(accountId);
  }

  public Optional<Account> getAccountByLogin(String login) {
    return accountService.getAccountByLogin(login);
  }

  public Optional<Account> getAccountByEmail(SetEmailToSendPasswordDto emailDto) {
    return accountService.getAccountByEmail(emailDto.getEmail());
  }

  public List<Account> getAccountList() {
    return accountService.getAccountList();
  }

  public String login(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
    return authenticationService.login(userCredentialsDto.getLogin(),
        userCredentialsDto.getPassword());
  }

  public void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel) {
    accountService.addAccessLevelToAccount(accountId, accessLevel);
    Account foundAccount = getAccountByAccountId(accountId).get();
    //mailService.sendEmailAboutAddingAccessLevel(foundAccount.getEmail(), foundAccount.getLocale());
  }

  public void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel) {
    accountService.removeAccessLevelFromAccount(accountId, accessLevel);
    Account foundAccount = getAccountByAccountId(accountId).get();
    //mailService.sendEmailAboutRemovingAccessLevel(foundAccount.getEmail(), foundAccount.getLocale());
  }

  public void changePassword(String login, String newPassword) {
    accountService.changePassword(login, newPassword);
  }

  public void changePasswordAsAdmin(String login, String newPassword) {
    accountService.changePasswordAsAdmin(login, newPassword);
  }

  public void editAccountInfo(String login, EditPersonInfoDto editPersonInfoDto) {
    accountService.editAccountInfo(login,
        DtoToEntityMapper.mapEditPersonInfoDtoToAccount(editPersonInfoDto));
  }

  public void editAccountInfoAsAdmin(String login,
                                     EditPersonInfoAsAdminDto editPersonInfoAsAdminDto) {
    accountService.editAccountInfoAsAdmin(login,
        DtoToEntityMapper.mapEditPersonInfoAsAdminDtoToAccount(editPersonInfoAsAdminDto));
  }

  public Account changeAccessLevel(Long accountId, AccessLevel accessLevel) {
    return accountService.changeAccessLevel(accountId, accessLevel);
  }

  public void confirmAccount(String token) {
    accountService.confirmAccount(token);
  }

  public String validatePasswordResetToken(String token) {
    return accountService.validatePasswordResetToken(token);
  }

  public void resetPassword(String login, ChangePasswordDto changePasswordDto) {
    accountService.resetPassword(login, changePasswordDto.getPassword());
  }

  public void sendResetPasswordEmail(SetEmailToSendPasswordDto emailDto) {
    accountService.sendResetPasswordEmail(emailDto.getEmail());
  }
}
