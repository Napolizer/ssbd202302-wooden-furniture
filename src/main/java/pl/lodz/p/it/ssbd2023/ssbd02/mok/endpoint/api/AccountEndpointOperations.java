package pl.lodz.p.it.ssbd2023.ssbd02.mok.endpoint.api;

import jakarta.ejb.Local;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.config.enums.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Mode;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangeLocaleDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.ChangePasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.FullNameDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.GoogleAccountRegisterDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.SetEmailToSendPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.UserCredentialsDto;

@Local
public interface AccountEndpointOperations {

  void registerAccount(AccountRegisterDto accountRegisterDto);

  AccountWithoutSensitiveDataDto createAccount(AccountCreateDto accountCreateDto);

  Account blockAccount(Long id);

  Account activateAccount(Long id);

  AccountWithoutSensitiveDataDto getAccountByAccountId(Long accountId);

  AccountWithoutSensitiveDataDto getAccountByLogin(String login);

  Account getAccountByEmail(SetEmailToSendPasswordDto emailDto);

  List<AccountWithoutSensitiveDataDto> getAccountList();

  List<String> login(UserCredentialsDto userCredentialsDto, String locale)
          throws AuthenticationException;

  AccountWithoutSensitiveDataDto addAccessLevelToAccount(Long accountId, String accessLevel);

  AccountWithoutSensitiveDataDto removeAccessLevelFromAccount(Long accountId, String accessLevel);

  AccountWithoutSensitiveDataDto changePassword(String login, String newPassword, String currentPassword);

  void changePasswordAsAdmin(String login);

  AccountWithoutSensitiveDataDto changePasswordFromLink(String token, String password, String currentPassword);

  AccountWithoutSensitiveDataDto editAccountInfo(String login, EditPersonInfoDto editPersonInfoDto);

  AccountWithoutSensitiveDataDto editAccountInfoAsAdmin(String login, EditPersonInfoDto editPersonInfoDto);

  AccountWithoutSensitiveDataDto changeAccessLevel(Long accountId, AccessLevelDto accessLevel);

  void confirmAccount(String token);

  String validateEmailToken(String token, TokenType tokenType);

  void resetPassword(String token, ChangePasswordDto changePasswordDto);

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

  void changeMode(String login, Mode mode);

  String generateTokenFromRefresh(String refreshToken);

  boolean checkIfUserIsForcedToChangePassword(String login);

  List<AccountWithoutSensitiveDataDto> findByFullNameLike(String fullName);

  List<FullNameDto> autoCompleteFullNames(String phrase);

  Mode getAccountMode(String login);
}
