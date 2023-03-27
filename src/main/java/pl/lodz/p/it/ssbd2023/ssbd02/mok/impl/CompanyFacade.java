package pl.lodz.p.it.ssbd2023.ssbd02.mok.impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Company;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.api.CompanyFacadeOperations;

import java.util.List;

@Stateless
public class CompanyFacade extends AbstractFacade<Company> implements CompanyFacadeOperations {
    @PersistenceContext(unitName = "ssbd02mok")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CompanyFacade() {
        super(Company.class);
    }

    @Override
    public Company findByNip(String nip) {
        return em.createNamedQuery(Company.FIND_BY_NIP, Company.class)
                .setParameter("nip", nip)
                .getSingleResult();
    }

    @Override
    public List<Company> findAllByCompanyName(String companyName) {
        return em.createNamedQuery(Company.FIND_ALL_BY_COMPANY_NAME, Company.class)
                .setParameter("companyName", companyName)
                .getResultList();
    }
}