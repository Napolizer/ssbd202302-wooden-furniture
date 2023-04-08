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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.RateFacadeOperations;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RateFacadeOperationsIT {
    @Inject
    private RateFacadeOperations rateFacadeOperations;

    @PersistenceContext(unitName = "ssbd02adminPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
                .addAsResource(new File("src/test/resources/"), "");
    }

    private static Rate rate;
    private static Rate secondRate;
    private static Rate thirdRate;
    private static Address address;
    private static Person person;
    private static Person secondPerson;
    private static Person thirdPerson;

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

    private static Rate buildRate(Person person) {
        return Rate.builder()
                .value(1)
                .person(person)
                .build();
    }

    @Test
    @Order(1)
    public void shouldReturnEmptyCollection() {
        assertEquals(rateFacadeOperations.findAll().size(),0);
    }

    @Test
    @Order(2)
    public void shouldCreateRate() {
        address = buildAddress();
        person = buildPerson(address);
        rate = buildRate(person);

        assertEquals(rateFacadeOperations.findAll().size(),0);

        Rate persistedRate = rateFacadeOperations.create(rate);
        assertEquals(persistedRate.getPerson(),person);
        assertEquals(rate.getPerson(),person);
        assertEquals(rateFacadeOperations.findAll().size(),1);
        assertEquals(rateFacadeOperations.findAll().get(0),persistedRate);
    }

    @Test
    @Order(3)
    public void failsToAddRateWithUnderstatedValue() {
        Rate rateWithUnderstatedValue = Rate.builder()
                .value(0)
                .person(buildPerson(buildAddress()))
                .build();
        assertThrows(EJBException.class, () -> rateFacadeOperations.create(rateWithUnderstatedValue));
    }

    @Test
    @Order(4)
    public void failsToAddRateWithSuprasedValue() {
        Rate rateWithSuprasedValue = Rate.builder()
                .value(6)
                .person(buildPerson(buildAddress()))
                .build();
        assertThrows(EJBException.class, () -> rateFacadeOperations.create(rateWithSuprasedValue));
    }

    @Test
    @Order(5)
    public void failsToAddRateWithNullValue() {
        Rate rateWithNullValue = Rate.builder()
                .value(null)
                .person(buildPerson(buildAddress()))
                .build();
        assertThrows(EJBException.class, () -> rateFacadeOperations.create(rateWithNullValue));
    }

    @Test
    @Order(6)
    public void failsToAddRateWithNullPerson() {
        Rate rateWithNullPerson = Rate.builder()
                .value(3)
                .person(null)
                .build();
        assertThrows(EJBException.class, () -> rateFacadeOperations.create(rateWithNullPerson));
    }

    @Test
    @Order(7)
    public void secondInitAddRatesAndFindById() {
        Address secondAddress = Address
                .builder()
                .country("Poland")
                .city("Lodz")
                .street("Koszykowa2")
                .postalCode("90-000")
                .streetNumber(24)
                .build();
        Address thirdAddress = Address
                .builder()
                .country("Poland")
                .city("Lodz")
                .street("Koszykowa3")
                .postalCode("90-000")
                .streetNumber(36)
                .build();

        secondPerson = Person.builder()
                .firstName("Kyle")
                .lastName("Doe")
                .address(secondAddress)
                .build();

        thirdPerson = Person.builder()
                .firstName("Adam")
                .lastName("Doe")
                .address(thirdAddress)
                .build();

        secondRate = Rate.builder()
                .value(3)
                .person(secondPerson)
                .build();
        thirdRate = Rate.builder()
                .value(1)
                .person(thirdPerson)
                .build();

        assertEquals(rateFacadeOperations.findAll().size(),1);

        assertEquals(rateFacadeOperations.create(secondRate),secondRate);
        assertEquals(rateFacadeOperations.create(thirdRate),thirdRate);

        Optional<Rate> result = rateFacadeOperations.find(secondRate.getId());

        assertEquals(result, rateFacadeOperations.find(secondRate.getId()));

        assertEquals(rateFacadeOperations.findAll().size(),3);
    }

   @Test
   @Order(8)
   public void shouldFindAllRatesByValue() {

        assertEquals(rateFacadeOperations.findAll().size(),3);

        List<Rate> ratesValueOne = rateFacadeOperations.findAllByValue(1);

        assertEquals(ratesValueOne.get(0).getValue(),rate.getValue());
        assertEquals(ratesValueOne.get(1).getValue(),thirdRate.getValue());

        assertEquals(rateFacadeOperations.findAllByValue(1).size(),2);
        assertEquals(rateFacadeOperations.findAllByValue(2).size(),0);
        assertEquals(rateFacadeOperations.findAllByValue(3).size(),1);
        assertEquals(rateFacadeOperations.findAllByValue(4).size(),0);
        assertEquals(rateFacadeOperations.findAllByValue(5).size(),0);
        assertEquals(rateFacadeOperations.findAll().size(),3);
   }

   @Test
   @Order(9)
   public void shouldFindAllRatesByPersonId() {
       assertEquals(rateFacadeOperations.findAll().size(),3);

       List<Rate> allRatesBySecondPerson = rateFacadeOperations.findAllByPersonId(secondPerson.getId());
       assertEquals(allRatesBySecondPerson.size(),1);
       assertEquals(allRatesBySecondPerson.get(0),secondRate);

       List<Rate> allRatesByThirdPerson = rateFacadeOperations.findAllByPersonId(thirdPerson.getId());
       assertEquals(allRatesByThirdPerson.size(),1);
       assertEquals(allRatesByThirdPerson.get(0),thirdRate);
   }

    @Test
    @Order(10)
    public void shouldUpdateRateValue() {
        assertEquals(secondRate.getValue(),3);
        secondRate.setValue(5);
        assertEquals(secondRate.getValue(),5);
        Rate updatedValueRate = rateFacadeOperations.update(secondRate);
        assertThat(updatedValueRate, is(notNullValue()));
        assertEquals(updatedValueRate.getValue(),secondRate.getValue());
        assertEquals(rateFacadeOperations.findAll().size(),3);
    }

    @Test
    @Order(11)
    public void clean() throws Exception {
        utx.begin();
        em.createQuery("DELETE FROM Rate");
        utx.commit();
    }
}
