package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.impl;

import static jakarta.ejb.TransactionAttributeType.REQUIRES_NEW;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
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

  public ProductGroupFacade() {
    super(ProductGroup.class);
  }

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

  @Override
  @PermitAll
  @TransactionAttribute(REQUIRES_NEW)
  public Optional<ProductGroup> findById(Long productGroupId) {
    try {
      return Optional.of(em.createNamedQuery(ProductGroup.FIND_BY_ID, ProductGroup.class)
              .setParameter("id", productGroupId)
              .getSingleResult());
    } catch (PersistenceException e) {
      return Optional.empty();
    }
  }

  @Override
  public Double getAverageRate(Long productGroupId) {
    return em.createNamedQuery(ProductGroup.GET_AVG_RATE, Double.class)
            .setParameter("productGroupId", productGroupId)
            .getSingleResult();
  }

  @Override
  @PermitAll
  public List<ProductGroup> findAll() {
    return super.findAll();
  }

  @Override
  @RolesAllowed({EMPLOYEE, CLIENT})
  public ProductGroup update(ProductGroup entity) {
    ProductGroup productGroup = super.update(entity);
    em.flush();
    return productGroup;
  }
}
