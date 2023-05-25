package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.ProductFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductServiceOperations;


@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ProductService implements ProductServiceOperations {

  @Inject
  private ProductFacadeOperations productFacade;

  @Override
  public Product create(Product entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Product archive(Long id, Product entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Product update(Long id, Product entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<Product> find(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Product> findAll() {
    throw new UnsupportedOperationException();
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
}
