package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.CategoryEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.CategoryServiceOperations;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
public class CategoryEndpoint implements CategoryEndpointOperations {

  @Inject
  private CategoryServiceOperations categoryService;

  @Override
  public List<Category> findAllByParentCategory(Category parentCategory) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<Category> find(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Category> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Category> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Category> findAllArchived() {
    throw new UnsupportedOperationException();
  }
}
