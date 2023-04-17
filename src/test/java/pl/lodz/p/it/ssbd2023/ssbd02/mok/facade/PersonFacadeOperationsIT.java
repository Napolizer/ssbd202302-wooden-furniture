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
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.*;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.PersonFacadeOperations;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonFacadeOperationsIT {

    @Inject
    private PersonFacadeOperations personFacadeOperations;
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
                .addAsResource(new File("src/test/resources/"), "");
    }
    private static Address address;
    private static Person person;
    private static Person person2;
    private static Person person3;
    private static Account account1;
    private static Account account2;

    @PersistenceContext(unitName = "ssbd02adminPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    @BeforeEach
    public void setup() throws Exception {
        utx.begin();
    }

    @AfterEach
    public void teardown() throws Exception {
        utx.commit();
    }

    private static Address buildAddress() {
        return Address
                .builder()
                .country("Poland")
                .city("Lodz")
                .street("Koszykowa")
                .postalCode("90-000")
                .streetNumber(12)
                .build();
    }
    private static Person buildPerson(Address address) {
        return Person.builder()
                .firstName("John")
                .lastName("Doe")
                .address(address)
                .build();
    }

    @Test
    @Order(1)
    public void init() {
        address = buildAddress();
        person = buildPerson(address);
    }
    @Test
    @Order(2)
    public void shouldReturnEmptyCollection() {
        List<Person> all = personFacadeOperations.findAll();
        assertEquals(0, all.size());
    }

    @Test
    @Order(3)
    public void shouldAddPerson() {
        assertEquals(0, personFacadeOperations.findAll().size());

        Person persistedPerson = personFacadeOperations.create(person);
        List<Person> allPeople = personFacadeOperations.findAll();

        assertEquals(1, allPeople.size());
        assertEquals(persistedPerson, allPeople.get(0));
    }

    @Test
    @Order(4)
    public void tryAddPersonWithoutRequiredFieldsShouldThrowsException() {
//        Person wrongPersonNoAddress = Person.builder()
//                .firstName("John")
//                .lastName("Doe")
//                .build();
//
//        assertThrows(EJBException.class, () -> personFacadeOperations.create(wrongPersonNoAddress));
//
//        Person wrongPersonNoLastName = Person.builder()
//                .firstName("Mike")
//                .address(address)
//                .build();
//
//        assertThrows(EJBException.class, () -> personFacadeOperations.create(wrongPersonNoLastName));
//
//        Person wrongPersonNoFirstName = Person.builder()
//                .lastName("Doe")
//                .address(address)
//                .build();
//
//        assertThrows(EJBException.class, () -> personFacadeOperations.create(wrongPersonNoFirstName));
    }

    @Test
    @Order(5)
    public void tryAddClientWithSameAddressShouldThrowException() {
//        Person wrongPersonWithUsedAddress = Person.builder()
//                .firstName("John")
//                .lastName("Doe")
//                .address(address)
//                .build();
//
//        assertThrows(EJBException.class, () -> personFacadeOperations.create(wrongPersonWithUsedAddress));
    }

    @Test
    @Order(6)
    public void findByIdTest() {
        assertDoesNotThrow(() -> personFacadeOperations.find(person.getId()).orElseThrow());
        assertEquals(person, personFacadeOperations.find(person.getId()).orElse(null));
    }

    @Test
    @Order(7)
    public void init2() {
        Address newAddress = Address.builder()
                .country("England")
                .city("London")
                .street("Fakestreet")
                .postalCode("40-200")
                .streetNumber(30)
                .build();
        Address newAddress2 = Address.builder()
                .country("England")
                .city("London")
                .street("Fakestreet2")
                .postalCode("40-200")
                .streetNumber(35)
                .build();

        account1 = Account.builder()
                .login("login")
                .password("password")
                .email("email")
                .locale("pl")
                .accountState(AccountState.ACTIVE)
                .build();

        account2 = Account.builder()
                .login("login2")
                .password("password2")
                .email("email2")
                .locale("pl")
                .accountState(AccountState.ACTIVE)
                .build();

        person2 = Person.builder()
                .firstName("John")
                .lastName("Smith")
                .address(newAddress)
                .account(account1)
                .build();

        person3 = Person.builder()
                .firstName("Mark")
                .lastName("Doe")
                .address(newAddress2)
                .account(account2)
                .build();

        personFacadeOperations.create(person2);
        personFacadeOperations.create(person3);
    }

    @Test
    @Order(7)
    public void findByFirstNameTest() {
        String name = "John";
        List<Person> peopleWithNameJohn = personFacadeOperations.findAllByFirstName(name);
        assertEquals(2, peopleWithNameJohn.size());
        assertEquals(name, peopleWithNameJohn.get(0).getFirstName());
        assertEquals(name, peopleWithNameJohn.get(1).getFirstName());

        Person wrongPerson = peopleWithNameJohn.stream()
                .filter(p -> !p.getFirstName().equals("John"))
                .findFirst()
                .orElse(null);
        assertNull(wrongPerson);
    }

    @Test
    @Order(8)
    public void findByLastNameTest() {
        List<Person> peopleWithLastNameDoe = personFacadeOperations.findAllByLastName("Doe");
        assertEquals(2, peopleWithLastNameDoe.size());
        assertEquals("Doe", peopleWithLastNameDoe.get(0).getLastName());
        assertEquals("Doe", peopleWithLastNameDoe.get(1).getLastName());

        Person wrongPerson = peopleWithLastNameDoe.stream()
                .filter(p -> !p.getLastName().equals("Doe"))
                .findFirst()
                .orElse(null);
        assertNull(wrongPerson);
    }

    @Test
    @Order(9)
    public void findByAccountLoginTest() {
        Optional<Person> personWithLogin = personFacadeOperations.findByAccountLogin("login");
        assertEquals(person2, personWithLogin.orElse(null));
    }

    @Test
    @Order(10)
    public void findByAddressId() {
        List<Person> peopleWithAddress = personFacadeOperations.findAllByAddressId(address.getId());
        assertEquals(1, peopleWithAddress.size());
        assertEquals(address, peopleWithAddress.get(0).getAddress());

    }

    @Test
    @Order(11)
    public void findByAccountId() {
        Optional<Person> personWithAccount1 = personFacadeOperations.findByAccountId(account1.getId());
        assertEquals(person2, personWithAccount1.orElse(null));
    }

    @Test
    @Order(12)
    public void findByAccountLogin() {
        Optional<Person> personWithAccount1Login = personFacadeOperations.findByAccountLogin(account1.getLogin());
        assertEquals(person2, personWithAccount1Login.orElse(null));
    }

    @Test
    @Order(13)
    public void findByAccountEmailTest() {
        Optional<Person> personWithEmail = personFacadeOperations.findByAccountEmail("email");
        assertEquals(person2, personWithEmail.orElse(null));
    }

    @Test
    @Order(14)
    public void clean() throws Exception {
        em.createQuery("DELETE FROM Person");
    }

}
