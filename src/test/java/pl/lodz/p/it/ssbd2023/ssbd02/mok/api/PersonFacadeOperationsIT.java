package pl.lodz.p.it.ssbd2023.ssbd02.mok.api;

import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Company;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import java.io.File;
import java.util.List;

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
                .addAsResource(new File("src/main/resources/"), "");
    }
    private static Address address;
    private static Person person;
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
//        address = buildAddress();
        Person wrongPersonNoAddress = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        assertThrows(EJBException.class, () -> personFacadeOperations.create(wrongPersonNoAddress));

        Person wrongPersonNoLastName = Person.builder()
                .firstName("Mike")
                .address(address)
                .build();

        assertThrows(EJBException.class, () -> personFacadeOperations.create(wrongPersonNoLastName));

        Person wrongPersonNoFirstName = Person.builder()
                .lastName("Doe")
                .address(address)
                .build();

        assertThrows(EJBException.class, () -> personFacadeOperations.create(wrongPersonNoFirstName));
    }

    @Test
    @Order(5)
    public void findByIdTest() {
        assertDoesNotThrow(() -> personFacadeOperations.find(person.getId()).orElseThrow());
        assertEquals(person, personFacadeOperations.find(person.getId()).orElse(null));
    }

    @Test
    @Order(6)
    public void init2() {
        Address newAddress = Address.builder()
                .country("England")
                .city("London")
                .street("Fakestreet")
                .postalCode("40-200")
                .streetNumber(30)
                .build();

        Person person2 = Person.builder()
                .firstName("John")
                .lastName("Smith")
                .address(address)
                .company(new Company("NIP", "Company"))
                .build();

        Person person3 = Person.builder()
                .firstName("Mark")
                .lastName("Doe")
                .address(newAddress)
                .company(new Company("NIP2", "Company2"))
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
    public void findByCompanyNipTest() {
        personFacadeOperations.findByCompanyNIP("NIP");
    }

}
