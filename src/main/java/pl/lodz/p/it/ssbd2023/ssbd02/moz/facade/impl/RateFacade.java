package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.RateFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.facade.AbstractFacade;

@Stateless
public class RateFacade extends AbstractFacade<Rate> implements RateFacadeOperations {

  @PersistenceContext(unitName = "ssbd02mozPU")
  private EntityManager em;

  public RateFacade() {
    super(Rate.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @Override
  @RolesAllowed(CLIENT)
  public Rate create(Rate entity) {
    Rate rate = super.create(entity);
    em.flush();
    return rate;
  }

  @Override
  @RolesAllowed(CLIENT)
  public void delete(Rate rate) {
    em.remove(em.merge(rate));
  }

  @Override
  public List<Rate> findAllByValue(Integer value) {
    return em.createNamedQuery(Rate.FIND_ALL_BY_VALUE, Rate.class)
        .setParameter("value", value)
        .getResultList();
  }

  @Override
  public List<Rate> findAllByPersonId(Long personId) {
    return em.createNamedQuery(Rate.FIND_ALL_BY_PERSON_ID, Rate.class)
        .setParameter("personId", personId)
        .getResultList();
  }
}
