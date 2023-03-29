package pl.lodz.p.it.ssbd2023.ssbd02.mok.impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.api.PersonFacadeOperations;

import java.util.List;
import java.util.Optional;

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
    public Optional<Person> findByCompanyNIP(String companyNIP) {
        try {
            return Optional.of(em.createNamedQuery(Person.FIND_BY_COMPANY_NIP, Person.class)
                    .setParameter("companyNip", companyNIP)
                    .getSingleResult());
        } catch (PersistenceException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Person> findByAccountLogin(String accountLogin) {
        try {
            return Optional.of(em.createNamedQuery(Person.FIND_BY_ACCOUNT_LOGIN, Person.class)
                    .setParameter("accountLogin", accountLogin)
                    .getSingleResult());
        } catch (PersistenceException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Person> findAllByAddressId(Long addressId) {
        return em.createNamedQuery(Person.FIND_ALL_BY_ADDRESS_ID, Person.class)
                .setParameter("addressId", addressId)
                .getResultList();
    }

    @Override
    public Optional<Person> findByAccountId(Long accountId) {
        try {
            return Optional.of(em.createNamedQuery(Person.FIND_BY_ACCOUNT_ID, Person.class)
                    .setParameter("accountId", accountId)
                    .getSingleResult());
        } catch (PersistenceException e) {
            return Optional.empty();
        }

    }

    @Override
    public List<Person> findAllByCompanyId(Long companyId) {
        return em.createNamedQuery(Person.FIND_ALL_BY_COMPANY_ID, Person.class)
                .setParameter("companyId", companyId)
                .getResultList();
    }

    @Override
    public Person create(Person entity) {
        if (entity.getAddress().getId() == null ||
                em.find(Address.class, entity.getAddress().getId()) == null) {
            em.persist(entity.getAddress());
        }

        if (entity.getCompany() != null) em.persist(entity.getCompany());
        if (entity.getAccount() != null) em.persist(entity.getAccount());
        return super.create(entity);
    }
}
