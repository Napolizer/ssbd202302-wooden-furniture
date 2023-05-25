package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.impl;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.ProductGroupFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.facade.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ProductGroupFacade extends AbstractFacade<ProductGroup>
        implements ProductGroupFacadeOperations {

  @PersistenceContext(unitName = "ssbd02mokPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }
}
