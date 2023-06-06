package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

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
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateDto;
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
  public ProductDto create(ProductCreateDto productCreateDto, byte[] image, String fileName) {
    Product product = productMapper.mapToProduct(productCreateDto);
    return productMapper.mapToProductDto(repeatTransactionWithOptimistic(() -> productService
                    .create(product, image, productCreateDto.getProductGroupId(), fileName)));
  }

  @Override
  public ProductDto archive(Long id, UpdateProductDto entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ProductDto update(Long id, UpdateProductDto entity) {
    throw new UnsupportedOperationException();
  }

  @PermitAll
  public ProductDto find(Long productId) {
    return repeatTransactionWithoutOptimistic(() -> productService.find(productId))
      .map(productMapper::mapToProductDto)
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
  protected boolean isLastTransactionRollback() {
    return productService.isLastTransactionRollback();
  }
}
