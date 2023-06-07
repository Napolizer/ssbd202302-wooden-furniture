package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.AccountFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.ProductFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.RateFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.RateServiceOperations;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RateService implements RateServiceOperations {

  @Inject
  private RateFacadeOperations rateFacade;
  @Inject
  private ProductFacadeOperations productFacade;
  @Inject
  private AccountFacadeOperations accountFacade;

  @Override
  public void delete(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  @RolesAllowed(CLIENT)
  public Rate create(String login, Integer rateValue, Long productId) {
    Account account = accountFacade.findByLogin(login)
            .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    Product product = productFacade.findById(productId)
            .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

    ProductGroup productGroup = product.getProductGroup();

    boolean doesProductHaveRateFromThisAccount = productGroup.getRates().stream()
            .anyMatch(rate -> rate.getAccount().equals(account));

    if (!doesProductHaveRateFromThisAccount) {
      return rateFacade.create(new Rate(rateValue, account));
    } else {
      throw ApplicationExceptionFactory.createProductAlreadyRatedException();
    }
  }

  @Override
  public Rate archive(Long id, Rate entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Rate update(Long id, Rate entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<Rate> find(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Rate> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Rate> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Rate> findAllArchived() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Rate> findAllByValue(Integer value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Rate> findAllByPersonId(Long personId) {
    throw new UnsupportedOperationException();
  }
}
