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
  public Product createProductWithNewImage(Product product, byte[] image, Long productGroupId, String fileName) {
    ProductGroup productGroup = productGroupFacade.findById(productGroupId)
            .orElseThrow(ApplicationExceptionFactory::createProductGroupNotFoundException);
    product.setProductGroup(productGroup);
    Product entity = productFacade.create(product);
    entity.getImage().setUrl(googleService.saveImageInStorage(image, fileName));
    return entity;
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public Product createProductWithExistingImage(Product product, Long productGroupId, Long imageProductId) {
    ProductGroup productGroup = productGroupFacade.findById(productGroupId)
            .orElseThrow(ApplicationExceptionFactory::createProductGroupNotFoundException);
    Product productWithImage = productFacade.findById(imageProductId)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

    if (!(productGroup.getId().equals(productWithImage.getProductGroup().getId())
            && product.getColor().equals(productWithImage.getColor())
            && product.getWoodType().equals(productWithImage.getWoodType()))) {
      throw ApplicationExceptionFactory.createIncompatibleProductImageException();
    }

    product.setProductGroup(productGroup);
    product.setImage(productWithImage.getImage());
    return productFacade.update(product);
  }

  @RolesAllowed(EMPLOYEE)
  public Product archive(Long id) {
    Product product = productFacade.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

    if (!product.getArchive().equals(false)) {
      throw ApplicationExceptionFactory.createIllegalProductStateChangeException();
    }

    product.setArchive(true);
    return productFacade.update(product);
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

  @Override
  @PermitAll
  public List<Product> findAllByProductGroupColorAndWoodType(Long productGroupId, Color color, WoodType woodType) {
    return productFacade.findAllByProductGroupColorAndWoodType(productGroupId, color, woodType);
  }

  @Override
  @PermitAll
  public List<Product> findAllByProductGroup(Long productGroupId) {
    return productFacade.findAllByProductGroup(productGroupId);
  }
}
