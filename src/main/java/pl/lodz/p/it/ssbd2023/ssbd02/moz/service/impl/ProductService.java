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
import jakarta.persistence.OptimisticLockException;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.GoogleServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.ProductFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.ProductGroupFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductServiceOperations;
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
public class ProductService extends AbstractService implements ProductServiceOperations {

  @Inject
  private ProductFacadeOperations productFacade;

  @Inject
  private ProductGroupFacadeOperations productGroupFacade;

  @Inject
  private GoogleServiceOperations googleService;

  @Override
  @RolesAllowed(EMPLOYEE)
  public Product create(Product product, byte[] image, Long productGroupId, String fileName) {
    ProductGroup productGroup = productGroupFacade.findById(productGroupId)
            .orElseThrow(ApplicationExceptionFactory::createProductGroupNotFoundException);
    product.setProductGroup(productGroup);
    Product entity = productFacade.create(product);
    entity.getImage().setUrl(googleService.saveImageInStorage(image, fileName));
    return entity;
  }

  @Override
  public Product archive(Long id, Product entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Product update(Long id, Product entity) {
    throw new UnsupportedOperationException();
  }

  @PermitAll
  public Optional<Product> find(Long id) {
    return productFacade.findById(id);
  }

  @PermitAll
  public List<Product> findAll() {
    return productFacade.findAll();
  }

  @Override
  public List<Product> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Product> findAllArchived() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Product> findAllByWoodType(WoodType woodType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Product> findAllByColor(Color color) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Product> findAllAvailable() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Product> findAllByPrice(Double minPrice, Double maxPrice) {
    throw new UnsupportedOperationException();
  }

  @RolesAllowed(EMPLOYEE)
  public Product editProduct(Long id, Product productWithChanges, String hash) {
    Product product = productFacade.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

    if (!CryptHashUtils.verifyVersion(product.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    product.update(productWithChanges);
    return productFacade.update(product);
  }
}
