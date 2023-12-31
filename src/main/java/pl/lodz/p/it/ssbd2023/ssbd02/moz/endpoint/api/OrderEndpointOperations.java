package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CancelOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.CreateOrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderStatsDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.order.OrderWithProductsDto;


@Local
public interface OrderEndpointOperations {

  List<OrderDto> findByAccountLogin(String login);

  List<OrderDetailsDto> findByState(OrderState orderState);

  OrderDto create(CreateOrderDto entity, String login);

  OrderWithProductsDto find(Long id);

  List<OrderDetailsDto> findAll();

  List<OrderDetailsDto> findAllPresent();

  List<OrderDetailsDto> findAllArchived();

  OrderDto cancelOrder(CancelOrderDto orderDto, String login);

  OrderDto cancelOrderAsEmployee(Long id, String hash);

  OrderDto observeOrder(Long orderId, String hash, String login);

  OrderDto changeOrderState(Long id, OrderState state, String hash);

  byte[] generateReport(String startDate, String endDate, String locale);

  List<OrderStatsDto> findOrderStats(String startDate, String endDate);

  List<OrderDetailsDto> findWithFilters(Double minPrice, Double maxPrice, Integer totalAmount, boolean isCompany);

  List<OrderDetailsDto> findAllOrdersDone();

  OrderWithProductsDto findAsClient(String name, Long id);
}
