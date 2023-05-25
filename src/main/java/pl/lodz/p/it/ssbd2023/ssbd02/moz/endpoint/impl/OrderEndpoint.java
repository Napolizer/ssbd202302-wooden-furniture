package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.UpdateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.OrderEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.OrderServiceOperations;


@Stateful
@TransactionAttribute(TransactionAttributeType.NEVER)
public class OrderEndpoint implements OrderEndpointOperations {

  @Inject
  private OrderServiceOperations orderService;

  @Override
  public List<OrderDto> findByAccountLogin(String login) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<OrderDto> findByState(OrderState orderState) {
    throw new UnsupportedOperationException();
  }

  @Override
  public OrderDto create(CreateOrderDto entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public OrderDto archive(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public OrderDto update(Long id, UpdateOrderDto entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<OrderDto> find(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<OrderDto> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<OrderDto> findAllPresent() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<OrderDto> findAllArchived() {
    throw new UnsupportedOperationException();
  }

  @Override
  public OrderDto cancelOrder(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void observeOrder(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public OrderDto changeStateOfOrder(Long id, OrderState state) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void generateReport() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<OrderDto> findWithFilters(Double orderPrice, Integer orderSize, boolean isCompany) {
    throw new UnsupportedOperationException();
  }
}
