package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.RateFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.RateServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.service.AbstractService;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RateService extends AbstractService implements RateServiceOperations {

  @Inject
  private RateFacadeOperations rateFacade;
  @Inject
  private AccountFacadeOperations accountFacade;

  @Override
  @RolesAllowed(CLIENT)
  public void delete(Long id, String login) {
    Rate rate = rateFacade.find(id)
            .orElseThrow(ApplicationExceptionFactory::createRateNotFoundException);
    if (!rate.getAccount().getLogin().equals(login)) {
      throw ApplicationExceptionFactory.createRateNotFoundException();
    }
    rateFacade.delete(rate);
  }

  @Override
  @RolesAllowed(CLIENT)
  public Rate update(Long id, Integer newRate, String login) {
    accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    Rate rate = rateFacade.find(id)
            .orElseThrow(ApplicationExceptionFactory::createRateNotFoundException);

    if (!rate.getAccount().getLogin().equals(login)) {
      //throw if rate isn't assigned by this account
      throw ApplicationExceptionFactory.createRateNotFoundException();
    }

    rate.setValue(newRate);
    return rateFacade.update(rate);
  }

}
