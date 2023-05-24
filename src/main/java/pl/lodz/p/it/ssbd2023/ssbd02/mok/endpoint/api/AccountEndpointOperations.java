package pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint.api;

import jakarta.ejb.Local;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountSearchSettings;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangeLocaleDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangePasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.GoogleAccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.SetEmailToSendPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper.AccountSearchSettingsDto;

@Local
public interface AccountEndpointOperations {

  void registerAccount(AccountRegisterDto accountRegisterDto);

  Account createAccount(AccountCreateDto accountCreateDto);

  void blockAccount(Long id);

  void activateAccount(Long id);

  Optional<Account> getAccountByAccountId(Long accountId);

  Optional<Account> getAccountByLogin(String login);

  Optional<Account> getAccountByEmail(SetEmailToSendPasswordDto emailDto);

  List<Account> getAccountList();

  List<String> login(UserCredentialsDto userCredentialsDto, String ip, String locale)
          throws AuthenticationException;

  void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel);

  void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel);

  Account changePassword(String login, String newPassword, String currentPassword);

  void changePasswordAsAdmin(String login);

  Account changePasswordFromLink(String token, String password, String currentPassword);

  void editAccountInfo(String login, EditPersonInfoDto editPersonInfoDto);

  void editAccountInfoAsAdmin(String login, EditPersonInfoDto editPersonInfoDto);

  Account changeAccessLevel(Long accountId, AccessLevel accessLevel);

  void confirmAccount(String token);

  String validateEmailToken(String token, TokenType tokenType);

  void resetPassword(String login, ChangePasswordDto changePasswordDto);

  void sendResetPasswordEmail(SetEmailToSendPasswordDto emailDto);

  void updateEmailAfterConfirmation(String login);

  void changeEmail(SetEmailToSendPasswordDto emailDto, Long accountId, String login, String version);

  Response handleGoogleRedirect(String code, String state, String ip, String locale);

  String getGoogleOauthLink();

  Response registerGoogleAccount(GoogleAccountRegisterDto googleAccountRegisterDto, String ip);

  Response handleGithubRedirect(String githubCode, String ip, String locale);

  String getGithubOauthLink();

  Response registerGithubAccount(AccountRegisterDto githubAccountRegisterDto, String ip);

  void changeLocale(Long accountId, ChangeLocaleDto changeLocaleDto);

  String generateTokenFromRefresh(String refreshToken);

  boolean checkIfUserIsForcedToChangePassword(String login);

  List<Account> findByFullNameLike(String fullName);

  List<Account> findByFullNameLikeWithPagination(AccountSearchSettingsDto accountSearchSettingsDto);

}
