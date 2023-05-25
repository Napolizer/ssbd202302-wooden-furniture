package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderState;

import java.util.List;
import java.util.Optional;

@Local
public interface OrderServiceOperations {

  List<Order> findByAccountLogin(String login);

  List<Order> findByState(OrderState orderState);

  Order create(Order entity);

  Order archive(Long id);

  Order update(Long id, Order entity);

  Optional<Order> find(Long id);

  List<Order> findAll();

  List<Order> findAllPresent();

  List<Order> findAllArchived();

  Order cancelOrder(Long id);

  void observeOrder(Long id);

  Order changeStateOfOrder(OrderState state);

  void generateReport();

  List<Order> findWithFilters(Double orderPrice, Integer orderSize, boolean isCompany);
}
