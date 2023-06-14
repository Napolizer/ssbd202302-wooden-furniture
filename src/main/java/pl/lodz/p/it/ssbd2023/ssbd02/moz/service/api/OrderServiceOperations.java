package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;

@Local
public interface OrderServiceOperations {

  List<Order> findByAccountLogin(String login);

  List<Order> findByState(OrderState orderState);

  Order create(Order entity, String login, Map<Long, Integer> orderedProductsMap);

  Order createWithGivenShippingData(Order entity, String login, Map<Long, Integer> orderedProductsMap);

  Order archive(Long id);

  Order update(Long id, Order entity);

  Optional<Order> find(Long id);

  List<Order> findAll();

  List<Order> findAllPresent();

  List<Order> findAllArchived();

  Order cancelOrder(Long id, String hash, String login);

  Order cancelOrderAsEmployee(Long id, String hash);

  Order observeOrder(Long id, String hash);

  Order changeOrderState(Long id, OrderState state, String hash);

  void generateReport();

  List<Order> findWithFilters(Double orderPrice, Integer orderSize, boolean isCompany);

  boolean isLastTransactionRollback();

}
