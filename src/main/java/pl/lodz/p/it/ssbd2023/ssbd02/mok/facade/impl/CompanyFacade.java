package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.impl;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Company;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.CompanyFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.facade.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class CompanyFacade extends AbstractFacade<Company> implements CompanyFacadeOperations {
  @PersistenceContext(unitName = "ssbd02mokPU")
  private EntityManager em;

  public CompanyFacade() {
    super(Company.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @Override
  public Optional<Company> findByNip(String nip) {
    return Optional.ofNullable(em.createNamedQuery(Company.FIND_BY_NIP, Company.class)
        .setParameter("nip", nip)
        .getSingleResult());
  }

  @Override
  public List<Company> findAllByCompanyName(String companyName) {
    return em.createNamedQuery(Company.FIND_ALL_BY_COMPANY_NAME, Company.class)
        .setParameter("companyName", companyName)
        .getResultList();
  }
}
