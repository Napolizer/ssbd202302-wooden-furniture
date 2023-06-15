package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper.OrderMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.UpdateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderProductDto;
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
  @RolesAllowed(EMPLOYEE)
  public List<OrderDetailsDto> findByState(OrderState orderState) {
    return repeatTransactionWithOptimistic(() -> orderService.findByState(orderState))
        .stream()
        .map(orderMapper::mapToOrderDetailsDto)
        .toList();
  }

  @Override
  @RolesAllowed(CLIENT)
  public OrderDto create(CreateOrderDto createOrderDto, String login) {
    Map<Long, Integer> orderedProductsMap = new HashMap<>();
    for (OrderProductDto orderedProduct : createOrderDto.getProducts()) {
      orderedProductsMap.put(orderedProduct.getProductId(), orderedProduct.getAmount());
    }
    Order created;
    if (createOrderDto.getShippingData() == null) {
      Order order = orderMapper.mapToOrderWithoutShippingData(createOrderDto);
      created = repeatTransactionWithOptimistic(() -> orderService.create(order, login, orderedProductsMap));
    } else {
      Order order = orderMapper.mapToOrder(createOrderDto);
      created = repeatTransactionWithOptimistic(
          () -> orderService.createWithGivenShippingData(order, login, orderedProductsMap));
    }
    return orderMapper.mapToOrderDto(created);
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
  public List<OrderDetailsDto> findAll() {
    return repeatTransactionWithOptimistic(() -> orderService.findAll())
        .stream()
        .map(orderMapper::mapToOrderDetailsDto)
        .toList();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<OrderDetailsDto> findAllPresent() {
    return repeatTransactionWithOptimistic(() -> orderService.findAllPresent())
        .stream()
        .map(orderMapper::mapToOrderDetailsDto)
        .toList();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<OrderDetailsDto> findAllArchived() {
    return repeatTransactionWithOptimistic(() -> orderService.findAllArchived())
        .stream()
        .map(orderMapper::mapToOrderDetailsDto)
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
  @RolesAllowed(EMPLOYEE)
  public OrderDto changeOrderState(Long id, OrderState state, String hash) {
    return orderMapper.mapToOrderDto(
        repeatTransactionWithoutOptimistic(() -> orderService.changeOrderState(id, state, hash)));
  }

  @Override
  public void generateReport() {
    throw new UnsupportedOperationException();
  }

  @Override
  @RolesAllowed(SALES_REP)
  public List<OrderDetailsDto> findWithFilters(Double minPrice, Double maxPrice,
                                               Integer totalAmount, boolean isCompany) {
    return repeatTransactionWithOptimistic(() -> orderService.findWithFilters(minPrice, maxPrice,
            totalAmount, isCompany))
            .stream()
            .map(orderMapper::mapToOrderDetailsDto)
            .toList();
  }

  @Override
  @RolesAllowed(SALES_REP)
  public List<OrderDetailsDto> findAllOrdersDone() {
    return repeatTransactionWithOptimistic(() -> orderService.findAllOrdersDone())
            .stream()
            .map(orderMapper::mapToOrderDetailsDto)
            .toList();
  }

  @Override
  protected boolean isLastTransactionRollback() {
    return orderService.isLastTransactionRollback();
  }
}
