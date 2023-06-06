package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.category.CategoryDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper.CategoryMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.CategoryEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.CategoryServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericEndpointExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.endpoint.AbstractEndpoint;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
@Interceptors({
    GenericEndpointExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class CategoryEndpoint extends AbstractEndpoint implements CategoryEndpointOperations {

  @Inject
  private CategoryServiceOperations categoryService;

  @Override
  @PermitAll
  public List<CategoryDto> findAllParentCategories() {
    return repeatTransactionWithoutOptimistic(() -> categoryService.findAllParentCategories())
            .stream().map(CategoryMapper::mapToCategoryDto).toList();
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

  @Override
  protected boolean isLastTransactionRollback() {
    return categoryService.isLastTransactionRollback();
  }
}
