package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Administrator;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Client;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Employee;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.SalesRep;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.security.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.security.TokenClaims;

public class TokenServiceTest {
  private TokenService tokenService;
  private Account accountAllRoles;
  private Account accountNoRoles;
  private Account administrator;
  private Account client;
  private Account employee;
  private Account salesRep;

  @BeforeEach
  public void setUp() {
    tokenService = new TokenService();
    accountAllRoles = Account.builder()
        .login("test")
        .password("test")
        .email("test@gmail.com")
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accessLevels(List.of(
            new Administrator(),
            new Client(),
            new Employee(),
            new SalesRep()))
        .newEmail("newmail@gmail.com")
        .build();

    accountNoRoles = Account.builder()
        .login("noroles")
        .password("noroles")
        .email("noroles@gmail.com")
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .build();

    administrator = Account.builder()
        .login("admin")
        .password("admin")
        .email("admin@gmail.com")
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accessLevels(List.of(new Administrator()))
        .build();

    client = Account.builder()
        .login("client")
        .password("client")
        .email("client@gmail.com")
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accessLevels(List.of(new Client()))
        .build();

    employee = Account.builder()
        .login("employee")
        .password("employee")
        .email("employee@gmail.com")
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accessLevels(List.of(new Employee()))
        .build();

    salesRep = Account.builder()
        .login("salesRep")
        .password("salesRep")
        .email("salesRep@gmail.com")
        .locale("pl")
        .accountState(AccountState.ACTIVE)
        .accessLevels(List.of(new SalesRep()))
        .build();
  }

  @Test
  public void properlyGeneratesTokenTest() {
    String token = tokenService.generateToken(accountAllRoles);
    assertThat(token, is(notNullValue()));
  }

  @Test
  public void shouldProperlyRetrieveAllRolesTest() {
    String token = tokenService.generateToken(accountAllRoles);
    TokenClaims tokenClaims = tokenService.getTokenClaims(token);
    assertThat(tokenClaims.getAccessLevels(), is(not(empty())));
    assertThat(tokenClaims.getAccessLevels().size(), is(4));
    assertThat(tokenClaims.getAccessLevels(), hasItems(
        instanceOf(Administrator.class),
        instanceOf(Client.class),
        instanceOf(Employee.class),
        instanceOf(SalesRep.class)
    ));
  }

  @Test
  public void shouldProperlyRetrieveNoRolesTest() {
    String token = tokenService.generateToken(accountNoRoles);
    TokenClaims tokenClaims = tokenService.getTokenClaims(token);
    assertThat(tokenClaims.getAccessLevels(), is(empty()));
  }

  @Test
  public void shouldProperlyRetrieveAdministratorRoleTest() {
    String token = tokenService.generateToken(administrator);
    TokenClaims tokenClaims = tokenService.getTokenClaims(token);
    assertThat(tokenClaims.getAccessLevels(), is(not(empty())));
    assertThat(tokenClaims.getAccessLevels().size(), is(1));
    assertThat(tokenClaims.getAccessLevels(), hasItem(instanceOf(Administrator.class)));
  }

  @Test
  public void shouldProperlyRetrieveClientRoleTest() {
    String token = tokenService.generateToken(client);
    TokenClaims tokenClaims = tokenService.getTokenClaims(token);
    assertThat(tokenClaims.getAccessLevels(), is(not(empty())));
    assertThat(tokenClaims.getAccessLevels().size(), is(1));
    assertThat(tokenClaims.getAccessLevels(), hasItem(instanceOf(Client.class)));
  }

  @Test
  public void shouldProperlyRetrieveEmployeeRoleTest() {
    String token = tokenService.generateToken(employee);
    TokenClaims tokenClaims = tokenService.getTokenClaims(token);
    assertThat(tokenClaims.getAccessLevels(), is(not(empty())));
    assertThat(tokenClaims.getAccessLevels().size(), is(1));
    assertThat(tokenClaims.getAccessLevels(), hasItem(instanceOf(Employee.class)));
  }

  @Test
  public void shouldProperlyRetrieveSalesRepRoleTest() {
    String token = tokenService.generateToken(salesRep);
    TokenClaims tokenClaims = tokenService.getTokenClaims(token);
    assertThat(tokenClaims.getAccessLevels(), is(not(empty())));
    assertThat(tokenClaims.getAccessLevels().size(), is(1));
    assertThat(tokenClaims.getAccessLevels(), hasItem(instanceOf(SalesRep.class)));
  }

  @Test
  public void shouldProperlyRetrieveLoginTest() {
    for (Account account : List.of(accountAllRoles, accountNoRoles, administrator, client, employee, salesRep)) {
      String token = tokenService.generateToken(account);
      TokenClaims tokenClaims = tokenService.getTokenClaims(token);
      assertThat(tokenClaims.getLogin(), is(account.getLogin()));
    }
  }

  @Test
  public void properlyGeneratesTokenForAccountConfirmationLinkTest() {
    String token = tokenService.generateTokenForEmailLink(accountAllRoles, TokenType.ACCOUNT_CONFIRMATION);
    assertThat(token, is(notNullValue()));
  }

  @Test
  public void properlyGeneratesTokenForPasswordResetLinkTest() {
    String token = tokenService.generateTokenForEmailLink(accountAllRoles, TokenType.PASSWORD_RESET);
    assertThat(token, is(notNullValue()));
  }

  @Test
  public void properlyGeneratesTokenForChangeEmailLinkTest() {
    String token = tokenService.generateTokenForEmailLink(accountAllRoles, TokenType.CHANGE_EMAIL);
    assertThat(token, is(notNullValue()));
  }

  @Test
  public void shouldProperlyValidateAccountConfirmationTokenTest() {
    String token = tokenService.generateTokenForEmailLink(accountAllRoles, TokenType.ACCOUNT_CONFIRMATION);
    assertThat(token, is(notNullValue()));
    assertDoesNotThrow(() -> tokenService.validateEmailToken(token, TokenType.ACCOUNT_CONFIRMATION, null));
    assertThat(tokenService.validateEmailToken(token, TokenType.ACCOUNT_CONFIRMATION,
            null), is(accountAllRoles.getLogin()));
  }

  @Test
  public void shouldProperlyValidateChangeEmailTokenTest() {
    String token = tokenService.generateTokenForEmailLink(accountAllRoles, TokenType.CHANGE_EMAIL);
    assertThat(token, is(notNullValue()));
    assertDoesNotThrow(() -> tokenService.validateEmailToken(token, TokenType.CHANGE_EMAIL,
            accountAllRoles.getNewEmail()));
    assertThat(tokenService.validateEmailToken(token, TokenType.CHANGE_EMAIL,
            accountAllRoles.getNewEmail()), is(accountAllRoles.getLogin()));
  }

  @Test
  public void shouldProperlyValidateResetPasswordTokenTest() {
    String token = tokenService.generateTokenForEmailLink(accountAllRoles, TokenType.PASSWORD_RESET);
    assertThat(token, is(notNullValue()));
    assertDoesNotThrow(() -> tokenService.validateEmailToken(token, TokenType.PASSWORD_RESET,
            accountAllRoles.getPassword()));
    assertThat(tokenService.validateEmailToken(token, TokenType.PASSWORD_RESET,
            accountAllRoles.getPassword()), is(accountAllRoles.getLogin()));
  }
}
