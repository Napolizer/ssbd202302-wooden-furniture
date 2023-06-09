package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper.OrderMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.UpdateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.OrderEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.OrderServiceOperations;
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
public class OrderEndpoint extends AbstractEndpoint implements OrderEndpointOperations {

  @Inject
  private OrderServiceOperations orderService;
  @Inject
  private OrderMapper orderMapper;

  @Override
  @RolesAllowed(CLIENT)
  public List<OrderDto> findByAccountLogin(String login) {
    return orderService.findByAccountLogin(login).stream()
            .map(orderMapper::mapToOrderDto)
            .toList();
  }

  @Override
  public List<OrderDto> findByState(OrderState orderState) {
    return repeatTransactionWithOptimistic(() -> orderService.findByState(orderState))
        .stream()
        .map(orderMapper::mapToOrderDto)
        .toList();
  }

  @Override
  @RolesAllowed(CLIENT)
  public CreateOrderDto create(CreateOrderDto createOrderDto) {
    Order order = orderMapper.mapToOrder(createOrderDto);
    Order created = repeatTransactionWithOptimistic(() -> orderService.create(order));
    return orderMapper.mapToCreateOrderDto(created);
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
  @RolesAllowed(EMPLOYEE)
  public List<OrderDto> findAll() {
    return repeatTransactionWithOptimistic(() -> orderService.findAll())
        .stream()
        .map(orderMapper::mapToOrderDto)
        .toList();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<OrderDto> findAllPresent() {
    return repeatTransactionWithOptimistic(() -> orderService.findAllPresent())
        .stream()
        .map(orderMapper::mapToOrderDto)
        .toList();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<OrderDto> findAllArchived() {
    return repeatTransactionWithOptimistic(() -> orderService.findAllArchived())
        .stream()
        .map(orderMapper::mapToOrderDto)
        .toList();
  }

  @Override
  @RolesAllowed(CLIENT)
  public OrderDto cancelOrder(OrderDto orderDto) {
    Order order = repeatTransactionWithOptimistic(
        () -> orderService.cancelOrder(orderDto.getId(), orderDto.getHash()));
    return orderMapper.mapToOrderDto(order);
  }

  @Override
  @RolesAllowed(CLIENT)
  public OrderDto observeOrder(OrderDto orderDto) {
    Order order = repeatTransactionWithOptimistic(
        () -> orderService.observeOrder(orderDto.getId(), orderDto.getHash()));
    return orderMapper.mapToOrderDto(order);
  }

  @Override
  public OrderDto changeOrderState(Long id, OrderState state) {
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

  @Override
  protected boolean isLastTransactionRollback() {
    return orderService.isLastTransactionRollback();
  }
}
