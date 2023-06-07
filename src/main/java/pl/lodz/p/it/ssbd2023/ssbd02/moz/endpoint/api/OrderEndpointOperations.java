package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.UpdateOrderDto;


@Local
public interface OrderEndpointOperations {

  List<OrderDto> findByAccountLogin(String login);

  List<OrderDto> findByState(OrderState orderState);

  CreateOrderDto create(CreateOrderDto entity);

  OrderDto archive(Long id);

  OrderDto update(Long id, UpdateOrderDto entity);

  Optional<OrderDto> find(Long id);

  List<OrderDto> findAll();

  List<OrderDto> findAllPresent();

  List<OrderDto> findAllArchived();

  OrderDto cancelOrder(OrderDto orderDto);

  OrderDto observeOrder(OrderDto orderDto);

  OrderDto changeOrderState(Long id, OrderState state);

  void generateReport();

  List<OrderDto> findWithFilters(Double orderPrice, Integer orderSize, boolean isCompany);
  List<OrderDto> findDeliveredCustomerOrders(Long accountId);
}
