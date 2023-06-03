package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.CategoryName;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.dto.mapper.DtoToEntityMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupInfoDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.ProductGroupEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductGroupServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericEndpointExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
@Interceptors({
    GenericEndpointExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class ProductGroupEndpoint implements ProductGroupEndpointOperations {

  @Inject
  private ProductGroupServiceOperations productGroupService;

  @Override
  @RolesAllowed(EMPLOYEE)
  public ProductGroupInfoDto create(ProductGroupCreateDto entity) {
    CategoryName category = DtoToEntityMapper.mapStringToCategoryName(entity.getCategoryName());
    return DtoToEntityMapper.mapProductGroupToProductGroupInfoDto(productGroupService
            .create(ProductGroup.builder().name(entity.getName()).build(), category));
  }

  @Override
  public ProductGroupInfoDto archive(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ProductGroupInfoDto update(Long id, ProductGroup entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<ProductGroupInfoDto> find(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductGroupInfoDto> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductGroupInfoDto> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ProductGroupInfoDto> findAllArchived() {
    throw new UnsupportedOperationException();
  }
}
