package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.impl;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.CategoryFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.facade.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class CategoryFacade extends AbstractFacade<Category> implements CategoryFacadeOperations {
  @PersistenceContext(unitName = "ssbd02mozPU")
  private EntityManager em;

  public CategoryFacade() {
    super(Category.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @Override
  public Category create(Category entity) {
    //Operation will be not supported for this facade
    throw new UnsupportedOperationException();
  }

  @Override
  public Category archive(Category entity) {
    //Operation will be not supported for this facade
    throw new UnsupportedOperationException();
  }

  @Override
  public Category update(Category entity) {
    //Operation will be not supported for this facade
    throw new UnsupportedOperationException();
  }

  @Override
  @PermitAll
  public Optional<Category> findByCategoryName(String categoryName) {
    try {
      return Optional.of(em.createNamedQuery(Category.FIND_BY_CATEGORY_NAME, Category.class)
              .setParameter("categoryName", categoryName)
              .getSingleResult());
    } catch (PersistenceException e) {
      return Optional.empty();
    }
  }

  @Override
  @PermitAll
  public List<Category> findAllParentCategories() {
    return em.createNamedQuery(Category.FIND_ALL_PARENT_CATEGORIES, Category.class)
            .getResultList();
  }
}
