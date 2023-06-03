package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.ProductGroupFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.ProductGroupFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.facade.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    ProductGroupFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class ProductGroupFacade extends AbstractFacade<ProductGroup>
        implements ProductGroupFacadeOperations {

  @PersistenceContext(unitName = "ssbd02mozPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public ProductGroup create(ProductGroup entity) {
    ProductGroup productGroup = super.create(entity);
    em.flush();
    return productGroup;
  }
}
