package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api;

import jakarta.ejb.Local;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.facade.Facade;

@Local
public interface OrderFacadeOperations extends Facade<Order> {
  List<Order> findByAccountLogin(String login);
  List<Order> findByState(OrderState orderState);
  List<Order> findWithFilters(Double orderPrice, Integer orderSize, boolean isCompany);

}
