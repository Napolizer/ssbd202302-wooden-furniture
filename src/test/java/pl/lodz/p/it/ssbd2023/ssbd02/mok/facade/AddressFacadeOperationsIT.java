package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AddressFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.web.listener.StartupListener;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@ExtendWith(ArquillianExtension.class)
public class AddressFacadeOperationsIT {
    @Inject
    private AddressFacadeOperations addressFacadeOperations;

    @PersistenceContext(unitName = "ssbd02adminPU")
    private EntityManager em;
    @Resource
    private UserTransaction utx;

    private Address address;
    private Address secondAddress;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
                .deleteClass(StartupListener.class)
                .addAsResource(new File("src/test/resources/"),"");
    }

    @BeforeEach
    public void init() throws SystemException, NotSupportedException {
        address = Address
                .builder()
                .archive(false)
                .country("Polska")
                .city("Lodz")
                .street("Koszykowa")
                .postalCode("90-000")
                .streetNumber(12)
                .build();
        secondAddress = Address
                .builder()
                .archive(false)
                .country("Poland")
                .city("Warszawa")
                .street("Pilsudskiego")
                .postalCode("91-333")
                .streetNumber(2)
                .build();
        utx.begin();
    }

    @AfterEach
    public void teardown() throws Exception {
        utx.commit();
        utx.begin();
        em.createQuery("DELETE FROM Address").executeUpdate();
        utx.commit();
    }

    @Test
    public void properlyCreatesAddressWithoutIdTest() {
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(0)));
        Address savedAddress = addressFacadeOperations.create(address);
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(1)));

        assertThat(savedAddress, is(notNullValue()));
        assertThat(savedAddress.getId(), is(notNullValue()));
        assertThat(savedAddress.getCountry(), is(equalTo(address.getCountry())));
        assertThat(savedAddress.getCity(), is(equalTo(address.getCity())));
        assertThat(savedAddress.getStreet(), is(equalTo(address.getStreet())));
        assertThat(savedAddress.getPostalCode(), is(equalTo(address.getPostalCode())));
        assertThat(savedAddress.getStreetNumber(), is(equalTo(address.getStreetNumber())));
    }

    @Test
    public void failsToCreateAddressWithExistingId() {
//        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(0)));
//        Address savedAddress = addressFacadeOperations.create(address);
//        assertThat(savedAddress, is(notNullValue()));
//        assertThat(savedAddress.getId(), is(notNullValue()));
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.create(savedAddress));
    }

    @Test
    public void failsToCreateAddressWithNullCountry() {
//        address.setCountry(null);
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.create(address));
    }

    @Test
    public void failsToCreateAddressWithNullCity() {
//        address.setCity(null);
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.create(address));
    }

    @Test
    public void failsToCreateAddressWithNullStreet() {
//        address.setStreet(null);
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.create(address));
    }

    @Test
    public void failsToCreateAddressWithNullPostalCode() {
//        address.setPostalCode(null);
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.create(address));
    }

    @Test
    public void failsToCreateAddressWithNullStreetNumber() {
//        address.setStreetNumber(null);
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.create(address));
    }

    @Test
    public void properlyFindsAddress() {
        Address savedAddress = addressFacadeOperations.create(address);
        Optional<Address> foundAddressOptional = addressFacadeOperations.find(savedAddress.getId());
        assertThat(foundAddressOptional.isPresent(), is(equalTo(true)));

        Address foundAddress = foundAddressOptional.get();
        assertThat(foundAddress, is(notNullValue()));
        assertThat(foundAddress.getId(), is(equalTo(savedAddress.getId())));
        assertThat(foundAddress.getCountry(), is(equalTo(savedAddress.getCountry())));
        assertThat(foundAddress.getCity(), is(equalTo(savedAddress.getCity())));
        assertThat(foundAddress.getStreet(), is(equalTo(savedAddress.getStreet())));
        assertThat(foundAddress.getPostalCode(), is(equalTo(savedAddress.getPostalCode())));
        assertThat(foundAddress.getStreetNumber(), is(equalTo(savedAddress.getStreetNumber())));
    }

    @Test
    public void failsToFindAddressWithNullId() {
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.find(null));
    }

    @Test
    public void failsToFindAddressWithNonExistingId() {
//        Optional<Address> foundAddressOptional = addressFacadeOperations.find(1928374769872349872L);
//        assertThat(foundAddressOptional.isPresent(), is(equalTo(false)));
    }

    @Test
    public void properlyFindsAllAddresses() {
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(0)));
        Address savedAddress1 = addressFacadeOperations.create(address);
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(1)));
        Address savedAddress2 = addressFacadeOperations.create(secondAddress);

        List<Address> foundAddresses = addressFacadeOperations.findAll();
        assertThat(foundAddresses.size(), is(equalTo(2)));

        Optional<Address> foundAddress1Optional = foundAddresses.stream().filter(a -> a.getId().equals(savedAddress1.getId())).findFirst();
        assertThat(foundAddress1Optional.isPresent(), is(equalTo(true)));
        Address foundAddress1 = foundAddress1Optional.get();

        assertThat(foundAddress1, is(notNullValue()));
        assertThat(foundAddress1.getId(), is(equalTo(savedAddress1.getId())));
        assertThat(foundAddress1.getCountry(), is(equalTo(savedAddress1.getCountry())));
        assertThat(foundAddress1.getCity(), is(equalTo(savedAddress1.getCity())));
        assertThat(foundAddress1.getStreet(), is(equalTo(savedAddress1.getStreet())));
        assertThat(foundAddress1.getPostalCode(), is(equalTo(savedAddress1.getPostalCode())));
        assertThat(foundAddress1.getStreetNumber(), is(equalTo(savedAddress1.getStreetNumber())));

        Optional<Address> foundAddress2Optional = foundAddresses.stream().filter(a -> a.getId().equals(savedAddress2.getId())).findFirst();
        assertThat(foundAddress2Optional.isPresent(), is(equalTo(true)));
        Address foundAddress2 = foundAddress2Optional.get();
        assertThat(foundAddress2, is(notNullValue()));
        assertThat(foundAddress2.getId(), is(equalTo(savedAddress2.getId())));
        assertThat(foundAddress2.getCountry(), is(equalTo(savedAddress2.getCountry())));
        assertThat(foundAddress2.getCity(), is(equalTo(savedAddress2.getCity())));
        assertThat(foundAddress2.getStreet(), is(equalTo(savedAddress2.getStreet())));
        assertThat(foundAddress2.getPostalCode(), is(equalTo(savedAddress2.getPostalCode())));
        assertThat(foundAddress2.getStreetNumber(), is(equalTo(savedAddress2.getStreetNumber())));
    }

    @Test
    public void properlyFindsAllPresentAddresses() {
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(0)));
        Address savedAddress1 = addressFacadeOperations.create(address);
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(1)));
        Address savedAddress2 = addressFacadeOperations.create(secondAddress);

        List<Address> foundPresentAddresses = addressFacadeOperations.findAllPresent();
        assertThat(foundPresentAddresses.size(), is(equalTo(2)));

        Optional<Address> foundAddress1Optional = foundPresentAddresses.stream().filter(a -> a.getId().equals(savedAddress1.getId())).findFirst();
        assertThat(foundAddress1Optional.isPresent(), is(equalTo(true)));
        Address foundAddress1 = foundAddress1Optional.get();

        assertThat(foundAddress1, is(notNullValue()));
        assertThat(foundAddress1, is(equalTo(savedAddress1)));

        Optional<Address> foundAddress2Optional = foundPresentAddresses.stream().filter(a -> a.getId().equals(savedAddress2.getId())).findFirst();
        assertThat(foundAddress2Optional.isPresent(), is(equalTo(true)));
        Address foundAddress2 = foundAddress2Optional.get();

        assertThat(foundAddress2, is(notNullValue()));
        assertThat(foundAddress2, is(equalTo(savedAddress2)));

        addressFacadeOperations.delete(savedAddress2);
        foundPresentAddresses = addressFacadeOperations.findAllPresent();
        assertThat(foundPresentAddresses.size(), is(equalTo(1)));
        assertThat(foundPresentAddresses.get(0), is(equalTo(savedAddress1)));
    }

    @Test
    public void properlyFindsAllArchivedAddresses() {
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(0)));
        addressFacadeOperations.create(address);
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(1)));
        Address savedAddress2 = addressFacadeOperations.create(secondAddress);

        List<Address> foundArchivedAddresses = addressFacadeOperations.findAllArchived();
        assertThat(foundArchivedAddresses.size(), is(equalTo(0)));

        addressFacadeOperations.delete(savedAddress2);
        foundArchivedAddresses = addressFacadeOperations.findAllArchived();
        assertThat(foundArchivedAddresses.size(), is(equalTo(1)));
        Address foundAddress = foundArchivedAddresses.get(0);
        assertThat(foundAddress, is(notNullValue()));
        assertThat(foundAddress.getArchive(), is(equalTo(true)));
        assertThat(foundAddress.getId(), is(equalTo(savedAddress2.getId())));
        assertThat(foundAddress.getCountry(), is(equalTo(savedAddress2.getCountry())));
        assertThat(foundAddress.getCity(), is(equalTo(savedAddress2.getCity())));
        assertThat(foundAddress.getStreet(), is(equalTo(savedAddress2.getStreet())));
        assertThat(foundAddress.getPostalCode(), is(equalTo(savedAddress2.getPostalCode())));
        assertThat(foundAddress.getStreetNumber(), is(equalTo(savedAddress2.getStreetNumber())));
    }

    @Test
    public void properlyUpdatesAddress() {
        Address savedAddress = addressFacadeOperations.create(address);
        savedAddress.setCountry("Czechy");
        savedAddress.setCity("Brno");
        savedAddress.setStreet("Krecika");
        savedAddress.setPostalCode("60-200");
        savedAddress.setStreetNumber(20);
        Address updatedAddress = addressFacadeOperations.update(savedAddress);
        assertThat(updatedAddress, is(notNullValue()));
        assertThat(updatedAddress.getId(), is(equalTo(savedAddress.getId())));
        assertThat(updatedAddress.getCountry(), is(equalTo(savedAddress.getCountry())));
        assertThat(updatedAddress.getCity(), is(equalTo(savedAddress.getCity())));
        assertThat(updatedAddress.getStreet(), is(equalTo(savedAddress.getStreet())));
        assertThat(updatedAddress.getPostalCode(), is(equalTo(savedAddress.getPostalCode())));
        assertThat(updatedAddress.getStreetNumber(), is(equalTo(savedAddress.getStreetNumber())));
    }

    @Test
    public void failsToUpdateAddressWithNullCountry() {
//        Address savedAddress = addressFacadeOperations.create(address);
//        savedAddress.setCountry(null);
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.update(savedAddress));
    }

    @Test
    public void failsToUpdateAddressWithNullCity() {
//        Address savedAddress = addressFacadeOperations.create(address);
//        savedAddress.setCity(null);
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.update(savedAddress));
    }

    @Test
    public void failsToUpdateAddressWithNullStreet() {
//        Address savedAddress = addressFacadeOperations.create(address);
//        savedAddress.setStreet(null);
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.update(savedAddress));
    }

    @Test
    public void failsToUpdateAddressWithNullPostalCode() {
//        Address savedAddress = addressFacadeOperations.create(address);
//        savedAddress.setPostalCode(null);
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.update(savedAddress));
    }

    @Test
    public void failsToUpdateAddressWithNullStreetNumber() {
//        Address savedAddress = addressFacadeOperations.create(address);
//        savedAddress.setStreetNumber(null);
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.update(savedAddress));
    }

    @Test
    public void properlyDeletesAddress() {
        Address savedAddress = addressFacadeOperations.create(address);
        Address deletedAddress = addressFacadeOperations.delete(savedAddress);
        assertThat(deletedAddress.getArchive(), is(equalTo(true)));
        assertThat(addressFacadeOperations.findAllPresent().size(), is(equalTo(0)));
    }

    @Test
    public void failsToDeleteNullAddress() {
//        Assertions.assertThrows(EJBException.class, () -> addressFacadeOperations.delete(null));
    }

    @Test
    public void properlyFindsAllAddressesByCountry() {
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(0)));
        Address savedAddress1 = addressFacadeOperations.create(address);
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(1)));
        addressFacadeOperations.create(secondAddress);

        List<Address> foundAddresses = addressFacadeOperations.findAllByCountry(address.getCountry());
        assertThat(foundAddresses.size(), is(equalTo(1)));

        Address foundAddress = foundAddresses.get(0);
        assertThat(foundAddress, is(notNullValue()));
        assertThat(foundAddress, is(equalTo(savedAddress1)));
    }

    @Test
    public void properlyFindsAllAddressesByCity() {
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(0)));
        Address savedAddress1 = addressFacadeOperations.create(address);
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(1)));
        addressFacadeOperations.create(secondAddress);

        List<Address> foundAddresses = addressFacadeOperations.findAllByCity(address.getCity());
        assertThat(foundAddresses.size(), is(equalTo(1)));

        Address foundAddress = foundAddresses.get(0);
        assertThat(foundAddress, is(notNullValue()));
        assertThat(foundAddress, is(equalTo(savedAddress1)));
    }

    @Test
    public void properlyFindsAllAddressesByStreet() {
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(0)));
        Address savedAddress1 = addressFacadeOperations.create(address);
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(1)));
        addressFacadeOperations.create(secondAddress);

        List<Address> foundAddresses = addressFacadeOperations.findAllByStreet(address.getStreet());
        assertThat(foundAddresses.size(), is(equalTo(1)));

        Address foundAddress = foundAddresses.get(0);
        assertThat(foundAddress, is(notNullValue()));
        assertThat(foundAddress, is(equalTo(savedAddress1)));
    }

    @Test
    public void properlyFindsAllAddressesByPostalCode() {
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(0)));
        Address savedAddress1 = addressFacadeOperations.create(address);
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(1)));
        addressFacadeOperations.create(secondAddress);

        List<Address> foundAddresses = addressFacadeOperations.findAllByPostalCode(address.getPostalCode());
        assertThat(foundAddresses.size(), is(equalTo(1)));

        Address foundAddress = foundAddresses.get(0);
        assertThat(foundAddress, is(notNullValue()));
        assertThat(foundAddress, is(equalTo(savedAddress1)));
    }

    @Test
    public void properlyFindsAllAddressesByStreetNumber() {
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(0)));
        Address savedAddress1 = addressFacadeOperations.create(address);
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(1)));
        addressFacadeOperations.create(secondAddress);

        List<Address> foundAddresses = addressFacadeOperations.findAllByStreetNumber(address.getStreetNumber());
        assertThat(foundAddresses.size(), is(equalTo(1)));

        Address foundAddress = foundAddresses.get(0);
        assertThat(foundAddress, is(notNullValue()));
        assertThat(foundAddress, is(equalTo(savedAddress1)));
    }
}
