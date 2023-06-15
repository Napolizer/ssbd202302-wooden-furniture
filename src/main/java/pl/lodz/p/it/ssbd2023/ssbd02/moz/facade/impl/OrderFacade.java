package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.impl;

import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
  @RolesAllowed(CLIENT)
  public List<Order> findByAccountLogin(String login) {
    return getEntityManager().createNamedQuery(Order.FIND_ACCOUNT_ORDERS, Order.class)
            .setParameter("login", login)
            .getResultList();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<Order> findAll() {
    return getEntityManager()
        .createQuery("SELECT entity FROM sales_order entity", Order.class)
        .getResultList();
  }

  @Override
  @RolesAllowed({EMPLOYEE, SALES_REP})
  public List<Order> findByState(OrderState orderState) {
    return getEntityManager().createNamedQuery(Order.FIND_BY_STATE, Order.class)
        .setParameter("orderState", orderState)
        .getResultList();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<Order> findAllPresent() {
    return getEntityManager()
        .createQuery("SELECT entity FROM sales_order entity WHERE entity.archive = false", Order.class)
        .getResultList();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<Order> findAllArchived() {
    return getEntityManager()
        .createQuery("SELECT entity FROM sales_order entity WHERE entity.archive = true", Order.class)
        .getResultList();
  }

  @Override
  public List<Order> findWithFilters(Double orderPrice, Integer orderSize, boolean isCompany) {
    throw new UnsupportedOperationException();
  }

  @Override
  @RolesAllowed(SALES_REP)
  public List<Object[]> findOrderStatsForReport(LocalDateTime startDate, LocalDateTime endDate) {
    return em.createNamedQuery(Order.FIND_ORDER_STATS_FOR_REPORT, Object[].class)
        .setParameter("startDate", startDate)
        .setParameter("endDate", endDate)
        .setParameter("createdState", OrderState.CREATED)
        .setParameter("cancelledState", OrderState.CANCELLED)
        .getResultList();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  @Transactional(REQUIRES_NEW)
  public Optional<Order> find(Long id) {
    return Optional.ofNullable(getEntityManager().find(Order.class, id));
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
