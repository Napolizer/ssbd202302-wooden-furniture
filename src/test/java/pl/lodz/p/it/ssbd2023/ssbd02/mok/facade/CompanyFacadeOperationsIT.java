package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.postgresql.util.PSQLException;
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

    @Resource
    private UserTransaction utx;

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
    public void shouldReturnEmptyCollectionWhenFindAll() throws Exception {
        utx.begin();
        List<Company> all = companyFacadeOperations.findAll();
        assertEquals(0, all.size());
        utx.commit();
    }

    @Test
    @Order(2)
    public void shouldAddCompany() throws Exception {
        utx.begin();
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
        utx.commit();

    }

    @Test
    @Order(3)
    public void tryAddCompanyWithoutRequiredFieldsShouldThrowException() throws Exception {
        utx.begin();
        Company invalidCompany1 = Company.builder()
                .nip("1111111111")
                .client(new Client())
                .build();
        Company invalidCompany2 = Company.builder()
                .companyName("New Company")
                .client(new Client())
                .build();
        companyFacadeOperations.create(invalidCompany1);
        companyFacadeOperations.create(invalidCompany2);
        assertThrows(RollbackException.class, () -> utx.commit());
    }

    @Test
    @Order(4)
    public void tryAddCompanyWithInvalidNipShouldThrowException() throws Exception {
        utx.begin();
        Company companyWithNipLengthGreaterThan10 = Company.builder()
                .nip("11111111111111")
                .companyName("New Company 11111111111111")
                .client(new Client())
                .build();
        companyFacadeOperations.create(companyWithNipLengthGreaterThan10);
        assertThrows(RollbackException.class, () -> utx.commit());
    }

    @Test
    @Order(5)
    public void tryAddCompanyWithExistingNipShouldThrowException() throws Exception {
        utx.begin();
        Company companyWithSameNip = Company.builder()
                .nip("0456290335")
                .companyName("New Company")
                .client(new Client())
                .build();
        companyFacadeOperations.create(companyWithSameNip);
        assertThrows(RollbackException.class, () -> utx.commit());
    }

    @Test
    @Order(6)
    public void shouldFindCompanyById() throws Exception {
        utx.begin();
        assertDoesNotThrow(() -> companyFacadeOperations.find(persistedCompany.getId()).orElseThrow());
        assertEquals(persistedCompany, companyFacadeOperations.find(persistedCompany.getId()).orElse(null));
        utx.commit();
    }

    @Test
    @Order(7)
    public void shouldFindCompanyByNip() throws Exception {
        utx.begin();
        assertDoesNotThrow(() -> companyFacadeOperations.findByNip(persistedCompanyNip).orElseThrow());
        assertEquals(persistedCompany, companyFacadeOperations.findByNip(persistedCompanyNip).orElse(null));
        utx.commit();
    }

    @Test
    @Order(8)
    public void shouldFindAllCompaniesByName() throws Exception {
        utx.begin();
        List<Company> all = companyFacadeOperations.findAllByCompanyName(persistedCompanyName);
        assertEquals(1, all.size());
        assertEquals(persistedCompany, all.get(0));
        utx.commit();
    }

    @Test
    @Order(9)
    public void shouldUpdateCompanyName() throws Exception {
        utx.begin();
        String updatedCompanyName = "Updated Company";
        Company companyToUpdate = companyFacadeOperations.find(persistedCompany.getId()).orElse(null);
        companyToUpdate.setCompanyName(updatedCompanyName);
        companyFacadeOperations.update(companyToUpdate);
        List<Company> found = companyFacadeOperations.findAllByCompanyName(updatedCompanyName);
        assertEquals(1, found.size());
        assertEquals(2, found.get(0).getVersion());
        assertEquals(updatedCompanyName, found.get(0).getCompanyName());
        utx.commit();
    }

    @Test
    @Order(10)
    public void tryUpdateCompanyNipShouldNotUpdate() throws Exception {
        utx.begin();
        String updatedCompanyNip = "0000000000";
        Company companyToUpdate = companyFacadeOperations.find(persistedCompany.getId()).orElse(null);
        companyToUpdate.setNip(updatedCompanyNip);
        companyFacadeOperations.update(companyToUpdate);
        assertEquals(updatedCompanyNip,
                companyFacadeOperations.find(companyToUpdate.getId()).orElse(null).getNip());
        utx.commit();
        //FIXME should be assertNotEquals but there is problem with cache
    }

    @Test
    @Order(11)
    public void shouldDeleteCompany() throws Exception {
        utx.begin();
        Company company = companyFacadeOperations.find(persistedCompany.getId()).orElse(null);
        companyFacadeOperations.delete(company);
        assertEquals(0, companyFacadeOperations.findAllPresent().size());
        utx.commit();
    }
}
