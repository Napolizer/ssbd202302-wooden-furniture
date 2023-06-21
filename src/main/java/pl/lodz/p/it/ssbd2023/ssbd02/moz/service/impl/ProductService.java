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
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderedProduct;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductHistory;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.ProductField;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.GoogleServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.AccountMozFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.OrderFacadeOperations;
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
  private OrderFacadeOperations orderFacade;

  @Inject
  private GoogleServiceOperations googleService;

  @Inject
  private Principal principal;

  @Inject
  private AccountMozFacadeOperations accountFacade;

  @Override
  @RolesAllowed(EMPLOYEE)
  public Product createProductWithNewImage(Product product, byte[] image, Long productGroupId, String fileName) {
    ProductGroup productGroup = productGroupFacade.findById(productGroupId)
            .orElseThrow(ApplicationExceptionFactory::createProductGroupNotFoundException);
    if (productGroup.getArchive()) {
      throw ApplicationExceptionFactory.createProductGroupIsArchiveException();
    }
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
    if (productGroup.getArchive()) {
      throw ApplicationExceptionFactory.createProductGroupIsArchiveException();
    }

    if (!(productGroup.getId().equals(productWithImage.getProductGroup().getId())
            && product.getColor().equals(productWithImage.getColor())
            && product.getWoodType().equals(productWithImage.getWoodType()))) {
      throw ApplicationExceptionFactory.createIncompatibleProductImageException();
    }

    product.setProductGroup(productGroup);
    product.setImage(productWithImage.getImage());
    product.setCreatedBy(accountFacade.findByLogin(principal.getName())
        .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException));
    return productFacade.update(product);
  }

  @RolesAllowed(EMPLOYEE)
  public Product archive(Long id) {
    Product product = productFacade.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

    if (product.getArchive()) {
      throw ApplicationExceptionFactory.createIllegalProductArchiveException();
    }

    product.getProductHistory().add(
            ProductHistory.builder().fieldName(ProductField.ARCHIVE).oldValue(0.0).newValue(1.0).build());
    product.setArchive(true);
    Product productAfterUpdate = productFacade.update(product);

    return productAfterUpdate;
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
  @PermitAll
  public List<Product> findAllByProductGroupColorAndWoodType(Long productGroupId, Color color, WoodType woodType) {
    return productFacade.findAllByProductGroupColorAndWoodType(productGroupId, color, woodType);
  }

  @Override
  @PermitAll
  public List<Product> findAllByProductGroup(Long productGroupId) {
    return productFacade.findAllByProductGroup(productGroupId);
  }

  @Override
  @PermitAll
  public List<Product> findAllByCategory(Long categoryId) {
    return productFacade.findAllByCategory(categoryId);
  }

  @RolesAllowed(EMPLOYEE)
  public Product editProduct(Long id, Product productWithChanges, String hash) {
    Product product = productFacade.findById(id)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

    if (!CryptHashUtils.verifyVersion(product.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    if (product.getArchive()) {
      throw ApplicationExceptionFactory.createIllegalProductArchiveException();
    }

    if (!Objects.equals(productWithChanges.getPrice(), product.getPrice())) {
      product.getProductHistory().add(ProductHistory.builder()
                      .fieldName(ProductField.PRICE)
                      .oldValue(product.getPrice())
                      .newValue(productWithChanges.getPrice())
                      .build());
    }

    if (!Objects.equals(productWithChanges.getAmount(), product.getAmount())) {
      product.getProductHistory().add(ProductHistory.builder()
                      .fieldName(ProductField.AMOUNT)
                      .oldValue(product.getAmount().doubleValue())
                      .newValue(productWithChanges.getAmount().doubleValue())
                      .build());
    }

    product.update(productWithChanges);
    return productFacade.update(product);
  }

  @Override
  @RolesAllowed(CLIENT)
  public List<OrderedProduct> findAllProductsBelongingToAccount(String login) {
    return orderFacade.findByAccountLogin(login).stream()
            .filter(o -> o.getOrderState().equals(OrderState.DELIVERED))
            .flatMap(o -> o.getOrderedProducts().stream())
            .toList();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<ProductHistory> findProductHistory(Long productId) {
    return productFacade.findById(productId).orElseThrow(ApplicationExceptionFactory::createProductNotFoundException)
            .getProductHistory();
  }
}
