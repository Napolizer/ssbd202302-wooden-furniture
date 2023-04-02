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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.PersonFacadeOperations;

import java.io.File;
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
}
