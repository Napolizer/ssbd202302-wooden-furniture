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
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper.OrderMapper;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CancelOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderStatsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderWithProductsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.UpdateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderedProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api.OrderEndpointOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.OrderServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericEndpointExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
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
    for (OrderedProductDto orderedProduct : createOrderDto.getProducts()) {
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
  @RolesAllowed(EMPLOYEE)
  public OrderWithProductsDto find(Long id) {
    return repeatTransactionWithOptimistic(() -> orderService.find(id))
        .map(orderMapper::mapToOrderWithProductsDto)
        .orElseThrow(ApplicationExceptionFactory::createOrderNotFoundException);
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
  public OrderDto cancelOrder(CancelOrderDto cancelOrderDto, String login) {
    Order order = repeatTransactionWithOptimistic(
        () -> orderService.cancelOrder(cancelOrderDto.getId(), cancelOrderDto.getHash(), login));
    return orderMapper.mapToOrderDto(order);
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public OrderDto cancelOrderAsEmployee(Long id, String hash) {
    Order order = repeatTransactionWithOptimistic(
        () -> orderService.cancelOrderAsEmployee(id, hash));
    return orderMapper.mapToOrderDto(order);
  }

  @Override
  @RolesAllowed(CLIENT)
  public OrderDto observeOrder(Long orderId, String hash, String login) {
    Order order = repeatTransactionWithOptimistic(
        () -> orderService.observeOrder(orderId, hash, login));
    return orderMapper.mapToOrderDto(order);
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public OrderDto changeOrderState(Long id, OrderState state, String hash) {
    return orderMapper.mapToOrderDto(
        repeatTransactionWithoutOptimistic(() -> orderService.changeOrderState(id, state, hash)));
  }

  @Override
  @RolesAllowed(SALES_REP)
  public byte[] generateReport(String startDate, String endDate, String locale) {
    if (locale == null || !(locale.equals(MessageUtil.LOCALE_PL) || locale.equals(MessageUtil.LOCALE_EN))) {
      throw ApplicationExceptionFactory.createInvalidLocaleException();
    }
    return repeatTransactionWithoutOptimistic(() -> orderService.generateReport(
            OrderMapper.mapToLocalDateTime(startDate),
            OrderMapper.mapToLocalDateTime(endDate),
            locale
    ));
  }

  @Override
  @RolesAllowed(SALES_REP)
  public List<OrderStatsDto> findOrderStats(String startDate, String endDate) {
    return repeatTransactionWithOptimistic(() -> orderService.findOrderStats(
        OrderMapper.mapToLocalDateTime(startDate),
        OrderMapper.mapToLocalDateTime(endDate)))
        .stream()
        .map(OrderMapper::mapObjectToOrderStatsDto)
        .toList();
  }

  @Override
  public List<OrderDto> findWithFilters(Double orderPrice, Integer orderSize, boolean isCompany) {
    throw new UnsupportedOperationException();
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
