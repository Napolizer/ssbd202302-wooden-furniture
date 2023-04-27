package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
public class AccountFacadeOperationsIT {
    @Inject
    private AccountFacadeOperations accountFacadeOperations;
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
                .addAsResource(new File("src/test/resources/"), "");
    }
    private Address address;
    private Person person;
    private Account account;

    @PersistenceContext(unitName = "ssbd02adminPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    @BeforeEach
    public void setup() {
        address = Address.builder()
                .country("England")
                .city("London")
                .street("Fakestreet")
                .postalCode("40-200")
                .streetNumber(30)
                .build();

        person = Person.builder()
                .firstName("John")
                .lastName("Smith")
                .address(address)
                .build();

        account = accountFacadeOperations.create(
                Account.builder()
                        .login("login")
                        .password("password")
                        .email("email")
                        .person(person)
                        .locale("pl")
                        .accountState(AccountState.ACTIVE)
                        .build()
        );
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
    public void shouldReturnEmptyCollection() {
        assertEquals(0, accountFacadeOperations.findAll().size());
    }

    @Test
    public void properlyGetsAllAccounts() {
        Address address2 = Address.builder()
                .country("Poland")
                .city("Warsaw")
                .street("Street")
                .postalCode("98-100")
                .streetNumber(20)
                .build();

        person.setAddress(address2);

        Account account2 = Account.builder()
                .login("login2")
                .password("password")
                .email("email")
                .person(person)
                .locale("pl")
                .accountState(AccountState.ACTIVE)
                .build();

        assertEquals(0, accountFacadeOperations.findAll().size());
        accountFacadeOperations.create(account);
        assertEquals(1, accountFacadeOperations.findAll().size());
        accountFacadeOperations.create(account2);
        assertEquals(2, accountFacadeOperations.findAll().size());
    }

    @Test
    public void properlyAddsAccount() {
        assertEquals(0, accountFacadeOperations.findAll().size());

        Account persistedAccount = accountFacadeOperations.create(account);

        List<Account> allAccounts = accountFacadeOperations.findAll();

        assertEquals(1, allAccounts.size());
        assertEquals(persistedAccount, allAccounts.get(0));
    }

    @Test
    public void failsToAddAccountWithoutLogin() {
        Account accountWithoutLogin = Account.builder()
                .password("password")
                .email("email")
                .person(person)
                .locale("pl")
                .accountState(AccountState.ACTIVE)
                .build();

        assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
    }

    @Test
    public void failsToAddAccountWithoutPassword() {
        Account accountWithoutLogin = Account.builder()
                .login("login")
                .email("email")
                .person(person)
                .locale("pl")
                .accountState(AccountState.ACTIVE)
                .build();

        assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
    }

    @Test
    public void failsToAddAccountWithoutEmail() {
        Account accountWithoutLogin = Account.builder()
                .login("login")
                .password("password")
                .person(person)
                .locale("pl")
                .accountState(AccountState.ACTIVE)
                .build();

        assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
    }

    @Test
    public void failsToAddAccountWithoutPerson() {
        Account accountWithoutLogin = Account.builder()
                .login("login")
                .password("password")
                .email("email")
                .locale("pl")
                .accountState(AccountState.ACTIVE)
                .build();

        assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
    }

    @Test
    public void failsToAddAccountWithoutLocale() {
        Account accountWithoutLogin = Account.builder()
                .login("login")
                .password("password")
                .email("email")
                .person(person)
                .accountState(AccountState.ACTIVE)
                .build();

        assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
    }

    @Test
    public void failsToAddAccountWithoutAccountState() {
        Account accountWithoutLogin = Account.builder()
                .login("login")
                .password("password")
                .email("email")
                .person(person)
                .locale("pl")
                .build();

        assertThrows(EJBException.class, () -> accountFacadeOperations.create(accountWithoutLogin));
    }

    @Test
    public void failsToAddNewAccountWithAlreadyAssignedAddress() {
        Person wrongPersonWithAlreadyAssignedAddress = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .address(address)
                .build();

        Account wrongAccountWithAlreadyAssignedAddress = Account.builder()
                .login("login")
                .password("password")
                .email("email")
                .person(wrongPersonWithAlreadyAssignedAddress)
                .locale("pl")
                .accountState(AccountState.ACTIVE)
                .build();

        assertDoesNotThrow(() -> accountFacadeOperations.create(account));
        assertThrows(EJBException.class, () -> accountFacadeOperations.create(wrongAccountWithAlreadyAssignedAddress));
    }

    @Test
    public void properlyGetsAccountById() {
        Account persistedAccount = accountFacadeOperations.create(account);
        assertEquals(persistedAccount, accountFacadeOperations.find(persistedAccount.getId()).orElse(null));
    }

    @Test
    public void failsToGetAccountByIdWhenAccountDoesNotExist() {
        assertThrows(EJBException.class, () -> accountFacadeOperations.find(0L));
    }

    @Test
    public void properlyGetsAllAccountsByFirstName() {
        Address address2 = Address.builder()
                .country("Poland")
                .city("Warsaw")
                .street("Street")
                .postalCode("98-100")
                .streetNumber(20)
                .build();

        person.setAddress(address2);

        Account account2 = Account.builder()
                .login("login2")
                .password("password")
                .email("email")
                .person(person)
                .locale("pl")
                .accountState(AccountState.ACTIVE)
                .build();

        Address address3 = Address.builder()
                .country("Poland")
                .city("Warsaw")
                .street("Street")
                .postalCode("98-100")
                .streetNumber(10)
                .build();

        Person person2 = Person.builder()
                .firstName("Jan")
                .lastName("Dzban")
                .address(address3)
                .build();

        Account account3 = Account.builder()
                .login("login2")
                .password("password")
                .email("email")
                .person(person2)
                .locale("pl")
                .accountState(AccountState.ACTIVE)
                .build();

        assertDoesNotThrow(() -> accountFacadeOperations.create(account));
        assertDoesNotThrow(() -> accountFacadeOperations.create(account2));
        assertDoesNotThrow(() -> accountFacadeOperations.create(account3));

        String nameJohn = "John";
        String nameJan = "Jan";
        String nameKizo = "Kizo";

        List<Account> accountsWithNameJohn = accountFacadeOperations.findAllByFirstName(nameJohn);
        assertEquals(2, accountsWithNameJohn.size());
        assertEquals(nameJohn, accountsWithNameJohn.get(0).getPerson().getFirstName());
        assertEquals(nameJohn, accountsWithNameJohn.get(1).getPerson().getFirstName());

        List<Account> accountsWithNameJan = accountFacadeOperations.findAllByFirstName(nameJan);
        assertEquals(1, accountsWithNameJan.size());
        assertEquals(nameJan, accountsWithNameJan.get(0).getPerson().getFirstName());

        List<Account> accountsWithNameKizo = accountFacadeOperations.findAllByFirstName(nameKizo);
        assertEquals(0, accountsWithNameKizo.size());
    }

    @Test
    public void properlyGetsAllAccountsByLastName() {
        Address address2 = Address.builder()
                .country("Poland")
                .city("Warsaw")
                .street("Street")
                .postalCode("98-100")
                .streetNumber(20)
                .build();

        person.setAddress(address2);

        Account account2 = Account.builder()
                .login("login2")
                .password("password")
                .email("email")
                .person(person)
                .locale("pl")
                .accountState(AccountState.ACTIVE)
                .build();

        Address address3 = Address.builder()
                .country("Poland")
                .city("Warsaw")
                .street("Street")
                .postalCode("98-100")
                .streetNumber(10)
                .build();

        Person person2 = Person.builder()
                .firstName("Jan")
                .lastName("Dzban")
                .address(address3)
                .build();

        Account account3 = Account.builder()
                .login("login2")
                .password("password")
                .email("email")
                .person(person2)
                .locale("pl")
                .accountState(AccountState.ACTIVE)
                .build();

        assertDoesNotThrow(() -> accountFacadeOperations.create(account));
        assertDoesNotThrow(() -> accountFacadeOperations.create(account2));
        assertDoesNotThrow(() -> accountFacadeOperations.create(account3));

        String lastNameSmith = "Smith";
        String lastNameDzban = "Dzban";
        String lastNameKizo = "Kizo";

        List<Account> accountsWithLastNameSmith = accountFacadeOperations.findAllByLastName(lastNameSmith);
        assertEquals(2, accountsWithLastNameSmith.size());
        assertEquals(lastNameSmith, accountsWithLastNameSmith.get(0).getPerson().getLastName());
        assertEquals(lastNameSmith, accountsWithLastNameSmith.get(1).getPerson().getLastName());

        List<Account> accountsWithLastNameDzban = accountFacadeOperations.findAllByLastName(lastNameDzban);
        assertEquals(1, accountsWithLastNameDzban.size());
        assertEquals(lastNameDzban, accountsWithLastNameDzban.get(0).getPerson().getLastName());

        List<Account> accountsWithLastNameKizo = accountFacadeOperations.findAllByLastName(lastNameKizo);
        assertEquals(0, accountsWithLastNameKizo.size());
    }

    @Test
    public void properlyGetsAccountByLogin() {
        Account persistedAccount = accountFacadeOperations.create(account);
        assertEquals(persistedAccount, accountFacadeOperations.findByLogin(persistedAccount.getLogin()).orElse(null));
    }

    @Test
    public void failsToGetAccountByLoginWhenAccountDoesNotExist() {
        assertThrows(EJBException.class, () -> accountFacadeOperations.findByLogin("login"));
    }

    @Test
    public void properlyGetsAccountsByAddressId() {
        assertDoesNotThrow(() -> accountFacadeOperations.create(account));
        List<Account> accountsWithSameAddress = accountFacadeOperations.findAllByAddressId(address.getId());
        assertEquals(1, accountsWithSameAddress.size());
        assertEquals(address, accountsWithSameAddress.get(0).getPerson().getAddress());

    }

    @Test
    public void properlyGetsAccountByEmail() {
        Account persistedAccount = accountFacadeOperations.create(account);
        assertEquals(persistedAccount, accountFacadeOperations.findByEmail(persistedAccount.getEmail()).orElse(null));
    }

    @Test
    public void failsToGetAccountByEmailWhenAccountDoesNotExist() {
        assertThrows(EJBException.class, () -> accountFacadeOperations.findByEmail("email"));
    }
}
