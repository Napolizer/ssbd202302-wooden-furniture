package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.impl;

import static jakarta.ejb.TransactionAttributeType.REQUIRES_NEW;
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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.ProductFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.ProductFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.facade.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    ProductFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class ProductFacade extends AbstractFacade<Product> implements ProductFacadeOperations {

  @PersistenceContext(unitName = "ssbd02mozPU")
  private EntityManager em;

  public ProductFacade() {
    super(Product.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public Product create(Product entity) {
    Product product = super.create(entity);
    em.flush();
    return product;
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public Product update(Product entity) {
    Product product = super.update(entity);
    em.flush();
    return product;
  }

  @Override
  public List<Product> findAllByWoodType(WoodType woodType) {
    return em.createNamedQuery(Product.FIND_ALL_BY_WOOD_TYPE, Product.class)
        .setParameter("woodType", woodType)
        .getResultList();
  }

  @Override
  public List<Product> findAllByColor(Color color) {
    return em.createNamedQuery(Product.FIND_ALL_BY_COLOR, Product.class)
        .setParameter("color", color)
        .getResultList();
  }

  @Override
  public List<Product> findAllAvailable() {
    return em.createNamedQuery(Product.FIND_ALL_AVAILABLE, Product.class)
        .getResultList();
  }

  @Override
  public List<Product> findAllByPrice(Double minPrice, Double maxPrice) {
    return em.createNamedQuery(Product.FIND_ALL_BY_PRICE, Product.class)
        .setParameter("minPrice", minPrice)
        .setParameter("maxPrice", maxPrice)
        .getResultList();
  }

  @Override
  @PermitAll
  @TransactionAttribute(REQUIRES_NEW)
  public Optional<Product> findById(Long productId) {
    try {
      return Optional.of(em.createNamedQuery(Product.FIND_BY_PRODUCT_ID, Product.class)
              .setParameter("id", productId)
              .getSingleResult());
    } catch (PersistenceException e) {
      return Optional.empty();
    }
  }

  @Override
  @PermitAll
  public List<Product> findAllByProductGroupColorAndWoodType(Long productGroupId, Color color, WoodType woodType) {
    return em.createNamedQuery(Product.FIND_ALL_BY_PRODUCT_GROUP_COLOR_AND_WOOD_TYPE, Product.class)
            .setParameter("productGroupId", productGroupId)
            .setParameter("color", color)
            .setParameter("woodType", woodType)
            .getResultList();
  }

  @Override
  @PermitAll
  public List<Product> findAllByProductGroup(Long productGroupId) {
    return em.createNamedQuery(Product.FIND_ALL_BY_PRODUCT_GROUP_ID, Product.class)
            .setParameter("productGroupId", productGroupId)
            .getResultList();
  }

  @Override
  @PermitAll
  public List<Product> findAllByCategory(Long categoryId) {
    return em.createNamedQuery(Product.FIND_ALL_BY_CATEGORY_ID, Product.class)
            .setParameter("categoryId", categoryId)
            .getResultList();
  }
}
