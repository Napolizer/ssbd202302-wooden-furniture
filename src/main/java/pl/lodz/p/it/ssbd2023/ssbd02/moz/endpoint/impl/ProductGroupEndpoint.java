package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.ProductGroupEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductGroupServiceOperations;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ProductGroupEndpoint implements ProductGroupEndpointOperations {

  @Inject
  private ProductGroupServiceOperations productGroupService;

  @Override
  public ProductGroupDto create(ProductGroupDto entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ProductGroupDto archive(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ProductGroupDto update(Long id, ProductGroup entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<ProductGroupDto> find(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductGroupDto> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductGroupDto> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductGroupDto> findAllArchived() {
    throw new UnsupportedOperationException();
  }
}
