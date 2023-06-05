package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.OptimisticLockException;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.OrderFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.OrderServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericServiceExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.service.AbstractService;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericServiceExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class OrderService extends AbstractService implements OrderServiceOperations {

  @Inject
  private OrderFacadeOperations orderFacade;

  @Override
  public List<Order> findByAccountLogin(String login) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Order> findByState(OrderState orderState) {
    throw new UnsupportedOperationException();
  }

  @Override
  @RolesAllowed(CLIENT)
  public Order create(Order order) {
    order.setOrderState(OrderState.CREATED);
    return orderFacade.create(order);
  }

  @Override
  public Order archive(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Order update(Long id, Order entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<Order> find(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Order> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Order> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Order> findAllArchived() {
    throw new UnsupportedOperationException();
  }

  @Override
  @RolesAllowed(CLIENT)
  public Order cancelOrder(Long id, String hash) {
    Order order = find(id).orElseThrow(ApplicationExceptionFactory::createOrderNotFoundException);

    if (order.getOrderState().equals(OrderState.IN_DELIVERY)) {
      throw ApplicationExceptionFactory.createOrderAlreadyInDeliveryException();
    } else if (order.getOrderState().equals(OrderState.DELIVERED)) {
      throw ApplicationExceptionFactory.createOrderAlreadyDeliveredException();
    } else if (order.getOrderState().equals(OrderState.CANCELLED)) {
      throw ApplicationExceptionFactory.createOrderAlreadyCancelledException();
    }

    if (!CryptHashUtils.verifyVersion(order.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    order.setOrderState(OrderState.CANCELLED);
    return orderFacade.update(order);
  }

  @Override
  @RolesAllowed(CLIENT)
  public Order observeOrder(Long id, String hash) {
    Order order = find(id).orElseThrow(ApplicationExceptionFactory::createOrderNotFoundException);

    if (order.getObserved()) {
      throw ApplicationExceptionFactory.createOrderAlreadyObservedException();
    }

    if (!CryptHashUtils.verifyVersion(order.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    order.setObserved(true);
    return orderFacade.update(order);
  }

  @Override
  public Order changeOrderState(OrderState state) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void generateReport() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Order> findWithFilters(Double orderPrice, Integer orderSize, boolean isCompany) {
    throw new UnsupportedOperationException();
  }
}
