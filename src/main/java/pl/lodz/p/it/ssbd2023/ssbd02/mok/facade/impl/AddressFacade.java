package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AddressFacadeOperations;

import java.util.List;

@Stateless
public class AddressFacade extends AbstractFacade<Address> implements AddressFacadeOperations {
    @PersistenceContext(unitName = "ssbd02mokPU")
    private EntityManager em;

    public AddressFacade() {
        super(Address.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Address> findAllByCountry(String country) {
        return em.createNamedQuery(Address.FIND_ALL_BY_COUNTRY, Address.class)
                .setParameter("country", country)
                .getResultList();
    }

    @Override
    public List<Address> findAllByCity(String city) {
        return em.createNamedQuery(Address.FIND_ALL_BY_CITY, Address.class)
                .setParameter("city", city)
                .getResultList();
    }

    @Override
    public List<Address> findAllByStreet(String street) {
        return em.createNamedQuery(Address.FIND_ALL_BY_STREET, Address.class)
                .setParameter("street", street)
                .getResultList();
    }

    @Override
    public List<Address> findAllByPostalCode(String postalCode) {
        return em.createNamedQuery(Address.FIND_ALL_BY_POSTAL_CODE, Address.class)
                .setParameter("postalCode", postalCode)
                .getResultList();
    }

    @Override
    public List<Address> findAllByStreetNumber(Integer streetNumber) {
        return em.createNamedQuery(Address.FIND_ALL_BY_STREET_NUMBER, Address.class)
                .setParameter("streetNumber", streetNumber)
                .getResultList();
    }
}
