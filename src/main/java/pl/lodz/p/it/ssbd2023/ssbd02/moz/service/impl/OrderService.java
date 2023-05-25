package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.OrderFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.OrderServiceOperations;


@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OrderService implements OrderServiceOperations {

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
  public Order create(Order entity) {
    throw new UnsupportedOperationException();
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
  public Order cancelOrder(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void observeOrder(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Order changeStateOfOrder(OrderState state) {
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
