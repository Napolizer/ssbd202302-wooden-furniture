package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.OrderFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.OrderFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.facade.AbstractFacade;


@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    OrderFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class OrderFacade extends AbstractFacade<Order> implements OrderFacadeOperations {

  @PersistenceContext(unitName = "ssbd02mozPU")
  private EntityManager em;

  @Override
  public List<Order> findByAccountLogin(String login) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Order> findByState(OrderState orderState) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Order> findWithFilters(Double orderPrice, Integer orderSize, boolean isCompany) {
    throw new UnsupportedOperationException();
  }

  @Override
  @RolesAllowed(CLIENT)
  public Order create(Order entity) {
    Order order = super.create(entity);
    em.flush();
    return order;
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }
}
