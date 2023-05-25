package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.ProductGroupFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductGroupServiceOperations;


@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ProductGroupService implements ProductGroupServiceOperations {

  @Inject
  private ProductGroupFacadeOperations productGroupFacade;

  @Override
  public ProductGroup create(ProductGroup entity) {
    throw new UnsupportedOperationException();
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
  public Optional<ProductGroup> find(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductGroup> findAll() {
    throw new UnsupportedOperationException();
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
