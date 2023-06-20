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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductHistory;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.ProductField;
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
  @RolesAllowed({EMPLOYEE, CLIENT})
  public Product update(Product entity) {
    Product product = super.update(entity);
    em.flush();
    return product;
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
  @RolesAllowed(EMPLOYEE)
  public List<ProductHistory> findAllDiscountsByEmployeeOfProductInCurrentMonth(Long productId, Long accountId) {
    return em.createNamedQuery(
            ProductHistory.FIND_ALL_DISCOUNTS_BY_EMPLOYEE_OF_PRODUCT_IN_CURRENT_MONTH, ProductHistory.class)
            .setParameter("productId", productId)
            .setParameter("accountId", accountId)
            .setParameter("price", ProductField.PRICE)
            .setParameter("now", LocalDateTime.now())
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
