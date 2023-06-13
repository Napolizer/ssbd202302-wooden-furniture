package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper.RateMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.rate.RateInputDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.RateEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductGroupServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.RateServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericServiceExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.endpoint.AbstractEndpoint;

@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
@Interceptors({
    GenericServiceExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class RateEndpoint extends AbstractEndpoint implements RateEndpointOperations {

  @Inject
  private RateServiceOperations rateService;

  @Inject
  private ProductGroupServiceOperations productGroupService;

  @Override
  @RolesAllowed(CLIENT)
  public void delete(String login, Long productGroupId) {
    productGroupService.removeRateFromProductGroup(login, productGroupId);
  }

  @Override
  @RolesAllowed(CLIENT)
  public RateDto create(String login, RateInputDto entity) {
    Rate rate = repeatTransactionWithOptimistic(() -> productGroupService
            .rateProductGroup(login, entity.getRate(), entity.getProductId()));

    return RateMapper.matToRateDto(rate);
  }

  @Override
  @RolesAllowed(CLIENT)
  public RateDto update(String login, RateInputDto entity) {
    Rate updatedRate = repeatTransactionWithOptimistic(() -> productGroupService
            .changeRateOnProductGroup(login, entity.getRate(), entity.getProductId()));

    return RateMapper.matToRateDto(updatedRate);
  }

  @Override
  protected boolean isLastTransactionRollback() {
    return rateService.isLastTransactionRollback();
  }
}
