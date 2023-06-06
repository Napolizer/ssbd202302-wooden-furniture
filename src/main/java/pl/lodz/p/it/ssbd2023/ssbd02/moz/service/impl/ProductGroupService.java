package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.CategoryName;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.CategoryFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.ProductGroupFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductGroupServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericServiceExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.service.AbstractService;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericServiceExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class ProductGroupService extends AbstractService implements ProductGroupServiceOperations {

  @Inject
  private ProductGroupFacadeOperations productGroupFacade;

  @Inject
  private CategoryFacadeOperations categoryFacade;

  @Override
  @RolesAllowed(EMPLOYEE)
  public ProductGroup create(ProductGroup productGroup, CategoryName categoryName) {
    Category category = categoryFacade.findByCategoryName(categoryName)
            .orElseThrow(ApplicationExceptionFactory::createCategoryNotFoundException);
    if (category.getParentCategory() == null) {
      throw ApplicationExceptionFactory.createParentCategoryNotAllowedException();
    }
    productGroup.setCategory(category);
    productGroup.setAverageRating(0.0);
    return productGroupFacade.create(productGroup);
  }

  @Override
  public ProductGroup archive(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ProductGroup update(Long id, ProductGroup entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  @PermitAll
  public Optional<ProductGroup> find(Long id) {
    return productGroupFacade.find(id);
  }

  @Override
  @PermitAll
  public List<ProductGroup> findAll() {
    return productGroupFacade.findAll();
  }

  @Override
  public List<ProductGroup> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductGroup> findAllArchived() {
    throw new UnsupportedOperationException();
  }
}
