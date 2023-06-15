package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api;

import jakarta.ejb.Local;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.facade.Facade;

@Local
public interface OrderFacadeOperations extends Facade<Order> {

  List<Order> findByAccountLogin(String login);

  List<Order> findByState(OrderState orderState);

  List<Order> findWithFilters(Double minPrice, Double maxPrice, Integer totalAmount,
                              OrderState orderState, boolean isCompany);
}
