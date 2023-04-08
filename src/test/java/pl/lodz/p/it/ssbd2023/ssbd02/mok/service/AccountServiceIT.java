package pl.lodz.p.it.ssbd2023.ssbd02.mok.service;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBException;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.IllegalAccountStateChangeException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.AccountNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.IllegalAccountStateChangeException;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.EditPersonInfoAsAdminDto;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.PersonFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.AccountService;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ArquillianExtension.class)
public class AccountServiceIT {
    @PersistenceContext(unitName = "ssbd02adminPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;
    @Inject
    private PersonFacadeOperations personFacadeOperations;
    @Inject
    private AccountService accountService;

    private Person person;
    private Person personToRegister;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
                .addPackages(true, "at.favre.lib")
                .addAsResource(new File("src/main/resources/"),"");
    }

    @BeforeEach
    public void setup() {
        person = personFacadeOperations.create(
                Person.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .address(Address.builder()
                                .country("Poland")
                                .city("Lodz")
                                .street("Koszykowa")
                                .postalCode("90-000")
                                .streetNumber(12)
                                .build())
                        .account(Account.builder()
                                .login("test")
                                .password("test")
                                .email("test@gmail.com")
                                .locale("pl")
                                .accountState(AccountState.ACTIVE)
                                .build())
                        .build()
        );
        personToRegister =
                Person.builder()
                        .firstName("Bob")
                        .lastName("Joe")
                        .address(Address.builder()
                                .country("Poland")
                                .city("Lodz")
                                .street("Koszykowa")
                                .postalCode("90-000")
                                .streetNumber(15)
                                .build())
                        .account(Account.builder()
                                .login("test123")
                                .password("test123")
                                .email("test@gmail.com")
                                .locale("pl")
                                .build())
                        .build();
    }

    @AfterEach
    public void teardown() throws Exception {
        utx.begin();
        em.createQuery("DELETE FROM Person").executeUpdate();
        em.createQuery("DELETE FROM Address").executeUpdate();
        em.createQuery("DELETE FROM Account").executeUpdate();
        utx.commit();
    }

    @Test
    public void properlyGetsAccountByLogin() {
        Optional<Account> accountOptional = accountService.getAccountByLogin(person.getAccount().getLogin());
        assertThat(accountOptional.isPresent(), is(equalTo(true)));
        assertThat(accountOptional.get(), is(equalTo(person.getAccount())));
    }

    @Test
    public void failsToGetAccountByLoginWhenLoginIsNull() {
        Optional<Account> accountOptional = accountService.getAccountByLogin(null);
        assertThat(accountOptional.isPresent(), is(equalTo(false)));
    }

    @Test
    public void failsToGetAccountByLoginWhenLoginIsEmpty() {
        Optional<Account> accountOptional = accountService.getAccountByLogin("");
        assertThat(accountOptional.isPresent(), is(equalTo(false)));
    }

    @Test
    public void failsToGetAccountByLoginWhenLoginDoesNotExist() {
        Optional<Account> accountOptional = accountService.getAccountByLogin("nonexistent");
        assertThat(accountOptional.isPresent(), is(equalTo(false)));
    }

    @Test
    public void properlyGetsAccountById() {
        Optional<Account> accountOptional = accountService.getAccountById(person.getId());
        assertThat(accountOptional.isPresent(), is(equalTo(true)));
        assertThat(accountOptional.get(), is(equalTo(person.getAccount())));
    }

    @Test
    public void failsToGetAccountByIdWhenIdDoesNotExist() {
        Optional<Account> accountOptional = accountService.getAccountById(0L);
        assertThat(accountOptional.isPresent(), is(equalTo(false)));
    }

    @Test
    public void failsToGetAccountByIdWhenIdIsNull() {
        assertThrows(EJBTransactionRolledbackException.class, () -> accountService.getAccountById(null));
    }

    @Test
    public void failsToGetAccountByIdWhenIdIsNegative() {
        Optional<Account> accountOptional = accountService.getAccountById(-1L);
        assertThat(accountOptional.isPresent(), is(equalTo(false)));
    }

    @Test
    public void failsToGetAccountByIdWhenIdIsMaxLong() {
        Optional<Account> accountOptional = accountService.getAccountById(Long.MAX_VALUE);
        assertThat(accountOptional.isPresent(), is(equalTo(false)));
    }

    @Test
    public void failsToGetAccountByIdWhenIdIsMinLong() {
        Optional<Account> accountOptional = accountService.getAccountById(Long.MIN_VALUE);
        assertThat(accountOptional.isPresent(), is(equalTo(false)));
    }

    @Test
    public void properlyGetsAllAccounts() {
        List<Account> accounts = accountService.getAccountList();
        assertThat(accounts.isEmpty(), is(false));
        assertThat(accounts.size(), is(equalTo(1)));
        assertThat(accounts.get(0), is(equalTo(person.getAccount())));
    }

    @Test
    public void properlyGetsEmptyAccountList() throws Exception {
        teardown();
        List<Account> accounts = accountService.getAccountList();
        assertThat(accounts.isEmpty(), is(true));
    }

    @Test
    public void properlyAddsNewAccessLevelToAccount() {
        AccessLevel newAccessLevel = new Client();

        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(0));
        accountService.addAccessLevelToAccount(person.getAccount().getId(), newAccessLevel);
        List<AccessLevel> accessLevels = personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels();
        assertThat(accessLevels.size(), equalTo(1));
        assertTrue(accessLevels.get(0) instanceof Client);
    }

    @Test
    public void failsToAddAccessLevelWhenAccessLevelIsAdded() {
        AccessLevel newAccessLevel = new Client();

        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(0));
        accountService.addAccessLevelToAccount(person.getAccount().getId(), newAccessLevel);
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(1));
        accountService.addAccessLevelToAccount(person.getAccount().getId(), newAccessLevel);
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(1));
    }

    @Test
    public void properlyRemovesAccessLevelFromAccount() {
        AccessLevel newAccessLevel = new Client();

        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(0));
        accountService.addAccessLevelToAccount(person.getAccount().getId(), newAccessLevel);
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(1));
        accountService.removeAccessLevelFromAccount(person.getAccount().getId(), newAccessLevel);
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(0));
    }

    @Test
    public void failsToRemoveAccessLevelWhenAccessLevelIsNotAdded() {
        AccessLevel accessLevelClient = new Client();
        AccessLevel accessLevelAdmin = new Administrator();

        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(0));
        accountService.addAccessLevelToAccount(person.getAccount().getId(), accessLevelClient);
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(1));
        accountService.removeAccessLevelFromAccount(person.getAccount().getId(), accessLevelAdmin);
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(1));
    }

    @Test
    public void properlyEditsAccountInfo() {
        EditPersonInfoDto editPersonInfoDto = new EditPersonInfoDto("Adam", "John", "Poland","Lodz","Koszykowa","90-200",24);
        assertEquals(person.getFirstName(), personFacadeOperations.findByAccountLogin("test").get().getFirstName());
        assertEquals(person.getLastName(), personFacadeOperations.findByAccountLogin("test").get().getLastName());
        assertEquals(person.getAddress().getStreetNumber(), personFacadeOperations.findAllByAddressId(person.getAddress().getId()).get(0).getAddress().getStreetNumber());
        accountService.editAccountInfo(person.getAccount().getLogin(), editPersonInfoDto);
        assertEquals(personFacadeOperations.findByAccountLogin("test").get().getFirstName(), "Adam");
        assertEquals(personFacadeOperations.findByAccountLogin("test").get().getLastName(), "John");
        assertEquals(personFacadeOperations.findByAccountLogin("test").get().getAddress().getStreetNumber(), 24);
    }


    @Test
    public void properlyEditsAccountInfoAsAdmin() {
        EditPersonInfoAsAdminDto editPersonInfoAsAdminDto = new EditPersonInfoAsAdminDto("Jack","Smith","Poland","Warsaw","Mickiewicza","92-100",15,"test1@gmail.com");
        assertEquals(person.getFirstName(), personFacadeOperations.findByAccountLogin("test").get().getFirstName());
        assertEquals(person.getLastName(), personFacadeOperations.findByAccountLogin("test").get().getLastName());
        assertEquals(person.getAddress().getStreetNumber(), personFacadeOperations.findAllByAddressId(person.getAddress().getId()).get(0).getAddress().getStreetNumber());
        assertEquals(person.getAccount().getEmail(),personFacadeOperations.findByAccountLogin("test").get().getAccount().getEmail());
        accountService.editAccountInfoAsAdmin(person.getAccount().getLogin(),editPersonInfoAsAdminDto);

        assertEquals(personFacadeOperations.findByAccountLogin("test").get().getFirstName(), "Jack");
        assertEquals(personFacadeOperations.findByAccountLogin("test").get().getLastName(), "Smith");
        assertEquals(personFacadeOperations.findByAccountLogin("test").get().getAddress().getCountry(), "Poland");
        assertEquals(personFacadeOperations.findByAccountLogin("test").get().getAddress().getCity(), "Warsaw");
        assertEquals(personFacadeOperations.findByAccountLogin("test").get().getAddress().getStreet(), "Mickiewicza");
        assertEquals(personFacadeOperations.findByAccountLogin("test").get().getAddress().getPostalCode(), "92-100");
        assertEquals(personFacadeOperations.findByAccountLogin("test").get().getAddress().getStreetNumber(), 15);
        assertEquals(personFacadeOperations.findByAccountLogin("test").get().getAccount().getEmail(), "test1@gmail.com");
    }

    @Test
    public void properlyRegistersAccountAsGuest() {
        assertDoesNotThrow(() -> accountService.registerAccountAsGuest(personToRegister));
        Account account = accountService.getAccountByLogin("test123").orElseThrow();
        assertEquals(2, accountService.getAccountList().size());
        assertEquals(AccountState.NOT_VERIFIED, account.getAccountState());
        assertEquals(false, account.getArchive());
        assertEquals(0, account.getFailedLoginCounter());
    }

    @Test
    public void properlyRegistersAccountAsAdmin() {
        personToRegister.getAccount().setAccountState(AccountState.ACTIVE);
        personToRegister.getAccount().setAccessLevels(List.of(new Client(), new Employee()));
        assertDoesNotThrow(() -> accountService.registerAccountAsAdmin(personToRegister));
        Account account = accountService.getAccountByLogin("test123").orElseThrow();
        assertEquals(2, accountService.getAccountList().size());
        assertEquals(AccountState.ACTIVE, account.getAccountState());
        assertEquals(2, account.getAccessLevels().size());
        assertEquals(false, account.getArchive());
        assertEquals(0, account.getFailedLoginCounter());
    }

    @Test
    public void failsToRegisterAccountWithSameLogin() {
        assertThrows(EJBException.class, () -> accountService.registerAccountAsGuest(person));
        assertEquals(1, accountService.getAccountList().size());
    }

    @Test
    public void properlyChangesPassword() {
        String newPassword = "newPassword";
        assertEquals("test", accountService.getAccountByLogin(person.getAccount().getLogin()).orElseThrow().getPassword());
        assertDoesNotThrow(() -> accountService.changePassword(person.getAccount().getLogin(), newPassword));
        assertEquals(newPassword, accountService.getAccountByLogin(person.getAccount().getLogin()).orElseThrow().getPassword());
    }

    @Test
    public void failsToChangePasswordWhenGivenOldPassword() {
        String oldPassword = person.getAccount().getPassword();
        assertEquals(oldPassword, accountService.getAccountByLogin(person.getAccount().getLogin()).orElseThrow().getPassword());
        accountService.changePassword(person.getAccount().getLogin(), oldPassword);
        assertEquals(oldPassword, accountService.getAccountByLogin(person.getAccount().getLogin()).orElseThrow().getPassword());
    }

    @Test
    public void properlyChangesPasswordAsAdmin() {
        String newPassword = "newPassword";
        assertEquals("test", accountService.getAccountByLogin(person.getAccount().getLogin()).orElseThrow().getPassword());
        assertDoesNotThrow(() -> accountService.changePasswordAsAdmin(person.getAccount().getLogin(), newPassword));
        assertEquals(newPassword, accountService.getAccountByLogin(person.getAccount().getLogin()).orElseThrow().getPassword());
    }

    @Test
    public void failsToChangePasswordAsAdminWhenGivenOldPassword() {
        String oldPassword = person.getAccount().getPassword();
        assertEquals(oldPassword, accountService.getAccountByLogin(person.getAccount().getLogin()).orElseThrow().getPassword());
        accountService.changePasswordAsAdmin(person.getAccount().getLogin(), oldPassword);
        assertEquals(oldPassword, accountService.getAccountByLogin(person.getAccount().getLogin()).orElseThrow().getPassword());
    }

    @Test
    public void properlyActivatesBlockedAccount() {
        personToRegister.getAccount().setAccountState(AccountState.BLOCKED);
        Account account = personFacadeOperations.create(personToRegister).getAccount();
        assertEquals(AccountState.BLOCKED, account.getAccountState());
        assertDoesNotThrow(() -> accountService.activateAccount(account.getId()));
        Account changed = accountService.getAccountByLogin(personToRegister.getAccount().getLogin()).orElseThrow();
        assertEquals(AccountState.ACTIVE, changed.getAccountState());
    }

    @Test
    public void properlyActivatesNotVerifiedAccount() {
        personToRegister.getAccount().setAccountState(AccountState.NOT_VERIFIED);
        Account account = personFacadeOperations.create(personToRegister).getAccount();
        assertEquals(AccountState.NOT_VERIFIED, account.getAccountState());
        assertDoesNotThrow(() -> accountService.activateAccount(account.getId()));
        Account changed = accountService.getAccountByLogin(personToRegister.getAccount().getLogin()).orElseThrow();
        assertEquals(AccountState.ACTIVE, changed.getAccountState());
    }

    @Test
    public void activateAlreadyActivatedAccountShouldThrowException() {
        personToRegister.getAccount().setAccountState(AccountState.ACTIVE);
        Account account = personFacadeOperations.create(personToRegister).getAccount();
        assertThrows(IllegalAccountStateChangeException.class,
                () -> accountService.activateAccount(account.getId()));
    }

    @Test
    public void activateInactiveAccountShouldThrowException() {
        personToRegister.getAccount().setAccountState(AccountState.INACTIVE);
        Account account = personFacadeOperations.create(personToRegister).getAccount();
        assertThrows(IllegalAccountStateChangeException.class,
                () -> accountService.activateAccount(account.getId()));
    }

    @Test
    public void activateNonExistingAccountShouldThrowException() {
        assertThrows(AccountNotFoundException.class,
                () -> accountService.activateAccount(Long.MAX_VALUE));

    }
}
