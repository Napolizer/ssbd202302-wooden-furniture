package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.UpdateOrderDto;


@Local
public interface OrderEndpointOperations {

  List<OrderDto> findByAccountLogin(String login);

  List<OrderDetailsDto> findByState(OrderState orderState);

  OrderDto create(CreateOrderDto entity, String login);

  OrderDto archive(Long id);

  OrderDto update(Long id, UpdateOrderDto entity);

  OrderDto find(Long id);

  List<OrderDetailsDto> findAll();

  List<OrderDetailsDto> findAllPresent();

  List<OrderDetailsDto> findAllArchived();

  OrderDto cancelOrder(OrderDto orderDto);

  OrderDto cancelOrderAsEmployee(Long id, String hash);

  OrderDto observeOrder(OrderDto orderDto);

  OrderDto changeOrderState(Long id, OrderState state, String hash);

  void generateReport();

  List<OrderDto> findWithFilters(Double orderPrice, Integer orderSize, boolean isCompany);
}
