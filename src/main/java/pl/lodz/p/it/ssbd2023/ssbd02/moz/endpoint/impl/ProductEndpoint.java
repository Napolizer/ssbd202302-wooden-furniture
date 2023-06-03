package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.AccountWithoutSensitiveDataDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper.ProductMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.CreateProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.UpdateProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.ProductEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericServiceExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.endpoint.AbstractEndpoint;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.ADMINISTRATOR;

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
  public ProductDto create(CreateProductDto entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ProductDto archive(Long id, UpdateProductDto entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ProductDto update(Long id, UpdateProductDto entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<ProductDto> find(Long id) {
    throw new UnsupportedOperationException();
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
