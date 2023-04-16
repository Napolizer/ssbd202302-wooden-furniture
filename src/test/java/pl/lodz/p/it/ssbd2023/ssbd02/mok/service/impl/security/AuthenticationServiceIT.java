package pl.lodz.p.it.ssbd2023.ssbd02.mok.service.impl.security;

import jakarta.annotation.Resource;
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
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.security.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.PersonFacadeOperations;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
public class AuthenticationServiceIT {
    @PersistenceContext(unitName = "ssbd02adminPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;
    @Inject
    private AuthenticationService authenticationService;
    @Inject
    private TokenService tokenService;
    @Inject
    private PersonFacadeOperations personFacadeOperations;
    private Person person;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
                .addPackages(true, "io.jsonwebtoken")
                .addPackages(true, "javax.xml.bind")
                .addAsResource(new File("src/test/resources/"), "");
    }

    @BeforeEach
    public void setUp() {
        person = Person.builder()
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
                                .accessLevels(List.of(new Client()))
                                .failedLoginCounter(0)
                                .build())
                        .build();
        personFacadeOperations.create(person);
    }

    @AfterEach
    public void tearDown() throws Exception {
        utx.begin();
        em.createQuery("DELETE FROM Person").executeUpdate();
        em.createQuery("DELETE FROM Address").executeUpdate();
        em.createQuery("DELETE FROM Account").executeUpdate();
        utx.commit();
    }

    @Test
    public void shouldProperlyLoginTest() {
        assertDoesNotThrow(() -> {
            String token = authenticationService.login(person.getAccount().getLogin(), person.getAccount().getPassword());
            assertNotNull(token);
            List<AccessLevel> accessLevels = tokenService.getTokenClaims(token).getAccessLevels();
            assertThat(accessLevels, is(not(empty())));
            assertThat(accessLevels.size(), is(equalTo(1)));
            assertThat(accessLevels.get(0), is(instanceOf(Client.class)));
        });
    }

    @Test
    public void shouldFailToLoginIfAccountWithGivenLoginDoesNotExist() {
        assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.login("nonexistent", "nonexistent");
        });
    }

    @Test
    public void shouldFailToLoginIfAccountWithGivenLoginIsInactive() {
        person.getAccount().setAccountState(AccountState.INACTIVE);
        personFacadeOperations.update(person);
        assertThrows(AccountIsInactiveException.class, () -> {
            authenticationService.login(person.getAccount().getLogin(), person.getAccount().getPassword());
        });
    }

    @Test
    public void shouldFailToLoginIfAccountWithGivenLoginIsBlocked() {
        person.getAccount().setAccountState(AccountState.BLOCKED);
        personFacadeOperations.update(person);
        assertThrows(AccountBlockedException.class, () -> {
            authenticationService.login(person.getAccount().getLogin(), person.getAccount().getPassword());
        });
    }

    @Test
    public void shouldFailToLoginIfAccountWithGivenLoginIsArchived() {
        person.getAccount().setArchive(true);
        personFacadeOperations.update(person);
        assertThrows(AccountArchiveException.class, () -> {
            authenticationService.login(person.getAccount().getLogin(), person.getAccount().getPassword());
        });
    }

    @Test
    public void shouldFailToLoginIfPasswordIsInvalid() {
        assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.login(person.getAccount().getLogin(), "invalid");
        });
    }

    @Test
    public void shouldFailToLoginIfLoginIsNull() {
        assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.login(null, "invalid");
        });
    }

    @Test
    public void shouldFailToLoginIfPasswordIsNull() {
        assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.login(person.getAccount().getLogin(), null);
        });
    }

    @Test
    public void shouldCorrectlyIncrementCounter() throws Exception {
        assertEquals(0, person.getAccount().getFailedLoginCounter());
        assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.login(person.getAccount().getLogin(), "wrongOne");
        });
        utx.begin();
        Person refreshedPerson = personFacadeOperations.findByAccountId(person.getAccount().getId())
                .orElseThrow();
        utx.commit();
        assertEquals(1, refreshedPerson.getAccount().getFailedLoginCounter());
    }

//    @Test
//    public void shouldBlockAccountAfterThreeFailureAttempts() {
//        assertEquals(0, person.getAccount().getFailedLoginCounter());
//        assertEquals(AccountState.ACTIVE, person.getAccount().getAccountState());
//
//        for (int i = 0; i < 3; i++) {
//            assertThrows(InvalidCredentialsException.class, () -> {
//                authenticationService.login(person.getAccount().getLogin(), "wrongOne");
//            });
//        }
//
//        Person refreshedPerson = personFacadeOperations.find(person.getId()).orElseThrow();
//        assertEquals(0, refreshedPerson.getAccount().getFailedLoginCounter());
//        assertEquals(AccountState.BLOCKED, refreshedPerson.getAccount().getAccountState());
//    }
}
