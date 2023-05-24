package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountSearchSettings;

@Local
public interface AccountServiceOperations {

  boolean isLastTransactionRollback();

  Optional<Account> getAccountByLogin(String login);

  Optional<Account> getAccountById(Long id);

  Optional<Account> getAccountByEmail(String email);

  List<Account> getAccountList();

  void addAccessLevelToAccount(Long accountId, AccessLevel accessLevel);

  void removeAccessLevelFromAccount(Long accountId, AccessLevel accessLevel);

  Account changeAccessLevel(Long accountId, AccessLevel accessLevel);

  void editAccountInfo(String login, Account accountWithChanges, String hash);

  void editAccountInfoAsAdmin(String login, Account accountWithChanges, String hash);

  void registerAccount(Account account);

  void registerGoogleAccount(Account account);

  void registerGithubAccount(Account account);

  Account createAccount(Account account);

  Account changePassword(String login, String newPassword, String currentPassword);

  Account changePasswordFromLink(String token, String newPassword, String currentPassword);

  void changePasswordAsAdmin(String login);

  void blockAccount(Long id);

  void activateAccount(Long id);

  void confirmAccount(String token);

  String validatePasswordResetToken(String token);

  String validatePasswordChangeToken(String token);

  String validateChangeEmailToken(String token);

  void resetPassword(String login, String password);

  void sendResetPasswordEmail(String email);

  Account updateEmailAfterConfirmation(String login);

  void changeEmail(String newEmail, Long accountId, String principal, String version);

  void changeLocale(Long accountId, String locale);

  boolean checkIfUserIsForcedToChangePassword(String login);

  String generateTokenFromRefresh(String refreshToken);

  List<Account> findByFullNameLike(String fullName);

  List<Account> findByFullNameLikeWithPagination(AccountSearchSettings accountSearchSettings);
}
