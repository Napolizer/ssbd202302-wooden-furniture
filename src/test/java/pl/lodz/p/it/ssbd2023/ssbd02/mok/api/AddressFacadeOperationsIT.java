package pl.lodz.p.it.ssbd2023.ssbd02.mok.api;

import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;

import java.io.File;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@ExtendWith(ArquillianExtension.class)
public class AddressFacadeOperationsIT {
    @Inject
    private AddressFacadeOperations addressFacadeOperations;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
                .addAsResource(new File("src/main/resources/"),"");
    }

    @Test
    public void properlyCreatesAddressTest() {
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(0)));
        Address address = Address
                .builder()
                .country("Poland")
                .city("Lodz")
                .street("Koszykowa")
                .postalCode("90-000")
                .streetNumber(12)
                .build();
        Address savedAddress = addressFacadeOperations.create(address);
        assertThat(savedAddress, is(notNullValue()));
        assertThat(savedAddress.getId(), is(notNullValue()));
        assertThat(addressFacadeOperations.findAll().size(), is(equalTo(1)));
    }
}
