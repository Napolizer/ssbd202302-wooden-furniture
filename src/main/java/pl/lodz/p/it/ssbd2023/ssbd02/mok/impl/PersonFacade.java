package pl.lodz.p.it.ssbd2023.ssbd02.mok.impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.api.PersonFacadeOperations;

import java.util.List;

@Stateless
public class PersonFacade extends AbstractFacade<Person> implements PersonFacadeOperations {

    @PersistenceContext(unitName = "ssbd02mokPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PersonFacade() {
        super(Person.class);
    }

    @Override
    public List<Person> findAllByFirstName(String firstName) {
        return em.createNamedQuery(Person.FIND_ALL_BY_FIRST_NAME, Person.class)
                .setParameter("firstName", firstName)
                .getResultList();
    }

    @Override
    public List<Person> findAllByLastName(String lastName) {
        return em.createNamedQuery(Person.FIND_ALL_BY_LAST_NAME, Person.class)
                .setParameter("lastName", lastName)
                .getResultList();
    }

    @Override
    public List<Person> findAllByCompanyNIP(String companyNIP) {
        return em.createNamedQuery(Person.FIND_ALL_BY_COMPANY_NIP, Person.class)
                .setParameter("companyNip", companyNIP)
                .getResultList();
    }

    @Override
    public List<Person> findAllByAccountLogin(String accountLogin) {
        return em.createNamedQuery(Person.FIND_ALL_BY_ACCOUNT_LOGIN, Person.class)
                .setParameter("accountLogin", accountLogin)
                .getResultList();
    }

    @Override
    public List<Person> findAllByAddressId(Long addressId) {
        return em.createNamedQuery(Person.FIND_ALL_BY_ADDRESS_ID, Person.class)
                .setParameter("addressId", addressId)
                .getResultList();
    }

    @Override
    public List<Person> findAllByAccountId(Long accountId) {
        return em.createNamedQuery(Person.FIND_ALL_BY_ACCOUNT_ID, Person.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    @Override
    public List<Person> findAllByCompanyId(Long companyId) {
        return em.createNamedQuery(Person.FIND_ALL_BY_COMPANY_ID, Person.class)
                .setParameter("companyId", companyId)
                .getResultList();
    }
}
