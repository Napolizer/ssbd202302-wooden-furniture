package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.OptimisticLockException;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.CategoryName;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.CategoryFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.ProductGroupFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductGroupServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericServiceExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;
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
  private AccountFacadeOperations accountFacade;

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
  @RolesAllowed(EMPLOYEE)
  public ProductGroup archive(Long id, String hash) {
    ProductGroup productGroup = productGroupFacade.findById(id)
        .orElseThrow(ApplicationExceptionFactory::createProductGroupNotFoundException);

    if (!CryptHashUtils.verifyVersion(productGroup.getVersion(), hash)) {
      throw new OptimisticLockException();
    }

    if (!productGroup.getArchive().equals(false)) {
      throw ApplicationExceptionFactory.createProductGroupAlreadyArchivedException();
    }

    productGroup.setArchive(true);
    return productGroupFacade.update(productGroup);
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public ProductGroup editProductGroupName(Long id, String name, String hash) {
    ProductGroup productGroup = productGroupFacade.findById(id)
        .orElseThrow(ApplicationExceptionFactory::createProductGroupNotFoundException);

    if (!CryptHashUtils.verifyVersion(productGroup.getVersion(), hash)) {
      throw new OptimisticLockException();
    }

    productGroup.setName(name);
    return productGroupFacade.update(productGroup);
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

  @Override
  @RolesAllowed(CLIENT)
  public Rate rateProductGroup(String login, Integer rateValue, Long productId) {
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    ProductGroup productGroup = productGroupFacade.findById(productId)
            .orElseThrow(ApplicationExceptionFactory::createProductGroupNotFoundException);


    boolean doesProductHaveRateFromThisAccount = productGroup.getRates().stream()
            .anyMatch(rate -> rate.getAccount().equals(account));

    if (!doesProductHaveRateFromThisAccount) {
      Rate rate = new Rate(rateValue, account);
      productGroup.getRates().add(rate);
      productGroup.updateAverageRating();

      productGroupFacade.update(productGroup);
      return rate;
    } else {
      throw ApplicationExceptionFactory.createProductAlreadyRatedException();
    }
  }
}
