package pl.lodz.p.it.ssbd2023.ssbd02.mok.impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.api.RateFacadeOperations;

import java.util.List;

@Stateless
public class RateFacade extends AbstractFacade<Rate> implements RateFacadeOperations {

    @PersistenceContext(unitName = "ssbd02mokPU")
    private EntityManager em;

    public RateFacade() {
        super(Rate.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Rate> findAllByValue(Integer value) {
        return em.createNamedQuery(Rate.FIND_ALL_BY_VALUE, Rate.class)
                .setParameter("value",value)
                .getResultList();
    }

    @Override
    public List<Rate> findAllByPersonId(Long personId) {
        return em.createNamedQuery(Rate.FIND_ALL_BY_PERSON_ID, Rate.class)
                .setParameter("personId",personId)
                .getResultList();
    }
}
