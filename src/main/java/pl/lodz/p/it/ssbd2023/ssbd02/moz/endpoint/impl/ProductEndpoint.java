package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import java.util.List;
import java.util.Optional;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.CreateProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.UpdateProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.ProductEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductServiceOperations;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ProductEndpoint implements ProductEndpointOperations {

  private final ProductServiceOperations productService;

  @Inject
  public ProductEndpoint(ProductServiceOperations productService) {
    this.productService = productService;
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
  public List<ProductDto> findAll() {
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
}
