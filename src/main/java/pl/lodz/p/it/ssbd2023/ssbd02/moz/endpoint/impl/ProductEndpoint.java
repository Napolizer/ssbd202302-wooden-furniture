package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

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
import java.util.List;
import java.util.stream.Collectors;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper.ProductMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.EditProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateWithImageDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.UpdateProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.ProductEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericServiceExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.endpoint.AbstractEndpoint;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
  GenericServiceExceptionsInterceptor.class,
  LoggerInterceptor.class
})
@DenyAll
public class ProductEndpoint extends AbstractEndpoint implements ProductEndpointOperations {

  @Inject
  private ProductServiceOperations productService;

  @Inject
  private ProductMapper productMapper;

  @PermitAll
  public List<ProductDto> findAll() {
    return repeatTransactionWithoutOptimistic(() -> productService.findAll()).stream()
            .map(productMapper::mapToProductDto)
            .collect(Collectors.toList());
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public ProductDto createProductWithNewImage(ProductCreateDto productCreateDto, byte[] image, String fileName) {
    Product product = ProductMapper.mapToProduct(productCreateDto);
    return productMapper.mapToProductDto(
            repeatTransactionWithOptimistic(() ->
                    productService.createProductWithNewImage(
                            product,
                            image,
                            productCreateDto.getProductGroupId(), fileName)));
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public ProductDto createProductWithExistingImage(ProductCreateWithImageDto productCreateWithImageDto) {
    Product product = ProductMapper.mapToProduct(productCreateWithImageDto);
    return productMapper.mapToProductDto(
            repeatTransactionWithOptimistic(() ->
                    productService.createProductWithExistingImage(
                            product,
                            productCreateWithImageDto.getProductGroupId(),
                            productCreateWithImageDto.getImageProductId())));
  }

  @RolesAllowed(EMPLOYEE)
  public Product archive(Long id) {
    return repeatTransactionWithOptimistic(() -> productService.archive(id));
  }

  @RolesAllowed(EMPLOYEE)
  public Product deArchive(Long id) {
    return repeatTransactionWithOptimistic(() -> productService.deArchive(id));
  }

  @Override
  public ProductDto update(Long id, UpdateProductDto entity) {
    throw new UnsupportedOperationException();
  }

  @PermitAll
  public ProductDto find(Long productId) {
    return repeatTransactionWithoutOptimistic(() -> productService.find(productId))
      .map(productMapper::mapToSingleProductDto)
      .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);
  }

  @Override
  public List<ProductDto> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductDto> findAllArchived() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductDto> findAllByWoodType(WoodType woodType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductDto> findAllByColor(Color color) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductDto> findAllAvailable() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductDto> findAllByPrice(Double minPrice, Double maxPrice) {
    throw new UnsupportedOperationException();
  }

  @Override
  @PermitAll
  public List<ProductDto> findAllByProductGroupColorAndWoodType(Long productGroupId, String color, String woodType) {
    return repeatTransactionWithoutOptimistic(() -> productService.findAllByProductGroupColorAndWoodType(
            productGroupId,
            ProductMapper.mapToColor(color),
            ProductMapper.mapToWoodType(woodType)))
            .stream().map(productMapper::mapToProductDto).toList();
  }

  @Override
  @PermitAll
  public List<ProductDto> findAllByProductGroupId(Long productGroupId) {
    return repeatTransactionWithoutOptimistic(() -> productService.findAllByProductGroup(
            productGroupId))
            .stream().map(productMapper::mapToProductDto).toList();
  }

  @Override
  @PermitAll
  public List<ProductDto> findAllByCategoryId(Long categoryId) {
    return repeatTransactionWithoutOptimistic(() -> productService.findAllByCategory(
            categoryId))
            .stream().map(productMapper::mapToProductDto).toList();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public EditProductDto editProduct(Long id, EditProductDto editProductDto) {
    Product product = repeatTransactionWithoutOptimistic(() -> productService.editProduct(id,
            ProductMapper.mapEditProductDtoToProduct(editProductDto), editProductDto.getHash()));
    return productMapper.mapToEditProductDto(product);
  }

  @Override
  @RolesAllowed(CLIENT)
  public List<ProductDto> findAllProductsBelongingToAccount(String login) {
    return repeatTransactionWithoutOptimistic(() ->
            productService.findAllProductsBelongingToAccount(login)).stream()
            .map(productMapper::mapToProductDto)
            .toList();
  }

  @Override
  protected boolean isLastTransactionRollback() {
    return productService.isLastTransactionRollback();
  }
}
