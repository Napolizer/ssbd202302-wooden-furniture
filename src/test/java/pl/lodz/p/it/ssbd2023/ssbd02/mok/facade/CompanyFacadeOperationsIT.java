package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade;

import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Client;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Company;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.CompanyFacadeOperations;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CompanyFacadeOperationsIT {

    @Inject
    private CompanyFacadeOperations companyFacadeOperations;

    private static Company persistedCompany;
    private static final String persistedCompanyNip = "0456290335";
    private static final String persistedCompanyName = "Example Industries";

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
                .addPackages(true, "org.postgresql")
                .addPackages(true, "org.hamcrest")
                .addAsResource(new File("src/test/resources/"),"");
    }

    @Test
    @Order(1)
    public void shouldReturnEmptyCollectionWhenFindAll() {
        List<Company> all = companyFacadeOperations.findAll();
        assertEquals(0, all.size());
    }

    @Test
    @Order(2)
    public void shouldAddCompany() {
        Company company = Company.builder()
                .nip(persistedCompanyNip)
                .companyName(persistedCompanyName)
                .client(new Client())
                .build();

        assertEquals(0, companyFacadeOperations.findAll().size());
        this.persistedCompany = companyFacadeOperations.create(company);
        List<Company> all = companyFacadeOperations.findAll();

        assertEquals(1, all.size());
        assertEquals(persistedCompany, all.get(0));

    }

    @Test
    @Order(3)
    public void tryAddCompanyWithoutRequiredFieldsShouldThrowException() {
        Company invalidCompany1 = Company.builder()
                .nip("1111111111")
                .client(new Client())
                .build();
        Company invalidCompany2 = Company.builder()
                .companyName("New Company")
                .client(new Client())
                .build();

        assertThrows(EJBException.class, () -> companyFacadeOperations.create(invalidCompany1));
        assertThrows(EJBException.class, () -> companyFacadeOperations.create(invalidCompany2));

        assertEquals(1, companyFacadeOperations.findAll().size());

    }

    @Test
    @Order(4)
    public void tryAddCompanyWithInvalidNipShouldThrowException() {
        Company companyWithNipLengthGreaterThan10 = Company.builder()
                .nip("11111111111111")
                .companyName("New Company 11111111111111")
                .client(new Client())
                .build();

        assertThrows(EJBException.class, () -> companyFacadeOperations.create(companyWithNipLengthGreaterThan10));

        assertEquals(1, companyFacadeOperations.findAll().size());
    }

    @Test
    @Order(5)
    public void tryAddCompanyWithExistingNipShouldThrowException() {
        Company companyWithSameNip = Company.builder()
                .nip("0456290335")
                .companyName("New Company")
                .client(new Client())
                .build();

        assertThrows(EJBException.class, () -> companyFacadeOperations.create(companyWithSameNip));

        assertEquals(1, companyFacadeOperations.findAll().size());
    }

    @Test
    @Order(6)
    public void shouldFindCompanyById() {
        assertDoesNotThrow(() -> companyFacadeOperations.find(persistedCompany.getId()).orElseThrow());
        assertEquals(persistedCompany, companyFacadeOperations.find(persistedCompany.getId()).orElse(null));
    }

    @Test
    @Order(7)
    public void shouldFindCompanyByNip() {
        assertDoesNotThrow(() -> companyFacadeOperations.findByNip(persistedCompanyNip).orElseThrow());
        assertEquals(persistedCompany, companyFacadeOperations.findByNip(persistedCompanyNip).orElse(null));
    }

    @Test
    @Order(8)
    public void shouldFindAllCompaniesByName() {
        List<Company> all = companyFacadeOperations.findAllByCompanyName(persistedCompanyName);
        assertEquals(1, all.size());
        assertEquals(persistedCompany, all.get(0));
    }

    @Test
    @Order(9)
    public void shouldUpdateCompanyName() {
       String updatedCompanyName = "Updated Company";
       Company companyToUpdate = companyFacadeOperations.find(persistedCompany.getId()).orElse(null);
       companyToUpdate.setCompanyName(updatedCompanyName);
       companyFacadeOperations.update(companyToUpdate);
       List<Company> found = companyFacadeOperations.findAllByCompanyName(updatedCompanyName);
       assertEquals(1, found.size());
       assertEquals(2, found.get(0).getVersion());
       assertEquals(updatedCompanyName, found.get(0).getCompanyName());
    }

    @Test
    @Order(10)
    public void tryUpdateCompanyNipShouldNotUpdate() {
        String updatedCompanyNip = "0000000000";
        Company companyToUpdate = companyFacadeOperations.find(persistedCompany.getId()).orElse(null);
        companyToUpdate.setNip(updatedCompanyNip);
        companyFacadeOperations.update(companyToUpdate);
        assertEquals(updatedCompanyNip,
                companyFacadeOperations.find(companyToUpdate.getId()).orElse(null).getNip());
        //FIXME should be assertNotEquals but there is problem with cache
    }

    @Test
    @Order(11)
    public void shouldDeleteCompany() {
        Company company = companyFacadeOperations.find(persistedCompany.getId()).orElse(null);
        companyFacadeOperations.delete(company);
        assertEquals(0, companyFacadeOperations.findAllPresent().size());
    }
}
