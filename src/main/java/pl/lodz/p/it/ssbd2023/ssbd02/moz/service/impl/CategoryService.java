package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.CategoryFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.CategoryServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericServiceExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.service.AbstractService;


@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericServiceExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class CategoryService extends AbstractService implements CategoryServiceOperations {

  @Inject
  private CategoryFacadeOperations categoryFacade;

  @Override
  @PermitAll
  public List<Category> findAllParentCategories() {
    return categoryFacade.findAllParentCategories();
  }
}
