package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl;

import jakarta.annotation.Resource;
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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.PersonFacadeOperations;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
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
                        .company(null)
                        .account(Account.builder()
                                .login("test")
                                .password("test")
                                .email("test@gmail.com")
                                .locale("pl")
                                .accountState(AccountState.ACTIVE)
                                .build())
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
        AccessLevel newAccessLevel = AccessLevel.builder()
                .level(AccessLevelName.CLIENT)
                .active(true)
                .build();
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(0));
        accountService.addNewAccessLevelToAccount(person.getId(), newAccessLevel);
        List<AccessLevel> accessLevels = personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels();
        assertThat(accessLevels.size(), equalTo(1));
        assertThat(accessLevels.get(0).getLevel(), equalTo(AccessLevelName.CLIENT));
        assertThat(accessLevels.get(0).getActive(), equalTo(true));
    }

    @Test
    public void failsToAddAccessLevelWhenAccessLevelIsAdded() {
        AccessLevel newAccessLevel = AccessLevel.builder()
                .level(AccessLevelName.CLIENT)
                .active(true)
                .build();
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(0));
        accountService.addNewAccessLevelToAccount(person.getId(), newAccessLevel);
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(1));
        accountService.addNewAccessLevelToAccount(person.getId(), newAccessLevel);
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(1));
    }

    @Test
    public void properlyRemovesAccessLevelFromAccount() {
        AccessLevel newAccessLevel = AccessLevel.builder()
                .level(AccessLevelName.CLIENT)
                .active(true)
                .build();
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(0));
        accountService.addNewAccessLevelToAccount(person.getId(), newAccessLevel);
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(1));
        accountService.removeAccessLevelFromAccount(person.getId(), newAccessLevel);
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(0));
    }

    @Test
    public void failsToRemoveAccessLevelWhenAccessLevelIsNotAdded() {
        AccessLevel accessLevelClient = AccessLevel.builder()
                .level(AccessLevelName.CLIENT)
                .active(true)
                .build();
        AccessLevel accessLevelAdmin = AccessLevel.builder()
                .level(AccessLevelName.ADMIN)
                .active(true)
                .build();
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(0));
        accountService.addNewAccessLevelToAccount(person.getId(), accessLevelClient);
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(1));
        accountService.removeAccessLevelFromAccount(person.getId(), accessLevelAdmin);
        assertThat(personFacadeOperations.find(person.getId()).orElseThrow().getAccount().getAccessLevels().size(), equalTo(1));
    }
}
