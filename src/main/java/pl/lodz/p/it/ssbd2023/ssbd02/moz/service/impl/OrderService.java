package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.CANCELLED;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderedProduct;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AccountServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.MailServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.OrderFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.ProductFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.OrderServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericServiceExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.service.AbstractService;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericServiceExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class OrderService extends AbstractService implements OrderServiceOperations {

  @Inject
  private OrderFacadeOperations orderFacade;
  @Inject
  private MailServiceOperations mailService;
  @Inject
  private AccountServiceOperations accountService;
  @Inject
  private ProductServiceOperations productService;
  @Inject
  private ProductFacadeOperations productFacade;

  @Override
  @RolesAllowed(CLIENT)
  public List<Order> findByAccountLogin(String login) {
    return orderFacade.findByAccountLogin(login);
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<Order> findByState(OrderState orderState) {
    return orderFacade.findByState(orderState);
  }

  @Override
  @RolesAllowed(CLIENT)
  public Order create(Order order, String login, Map<Long, Integer> orderedProductsMap) {
    Double totalPrice = 0.0;
    List<OrderedProduct> orderedProducts = new ArrayList<>();

    Account account = accountService.getAccountByLogin(login)
        .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    for (Long productId : orderedProductsMap.keySet()) {
      Product product = productService.find(productId)
          .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

      if (orderedProductsMap.get(productId) > product.getAmount()) {
        throw ApplicationExceptionFactory.createInvalidProductAmountException();
      }

      OrderedProduct orderedProduct = OrderedProduct.builder()
          .amount(orderedProductsMap.get(productId))
          .price(product.getPrice())
          .product(product)
          .order(order)
          .build();
      orderedProducts.add(orderedProduct);
      totalPrice += orderedProduct.getPrice() * orderedProductsMap.get(productId);

      if (product.getCreatedBy() != null) {
        if (product.getCreatedBy().getLogin().equals(login) && product.getUpdatedBy() == null) {
          throw ApplicationExceptionFactory.createProductCreatedByException();
        }
      }

      if (product.getUpdatedBy() != null) {
        if (product.getUpdatedBy().getLogin().equals(login)) {
          throw ApplicationExceptionFactory.createProductUpdatedByException();
        }
      }

      product.setAmount(product.getAmount() - orderedProduct.getAmount());
      product.setIsUpdatedBySystem(true);
      productFacade.update(product);
    }

    Address address = Address.builder()
            .country(account.getPerson().getAddress().getCountry())
            .city(account.getPerson().getAddress().getCity())
            .street(account.getPerson().getAddress().getStreet())
            .streetNumber(account.getPerson().getAddress().getStreetNumber())
            .postalCode(account.getPerson().getAddress().getPostalCode())
            .build();

    order.setRecipientFirstName(account.getPerson().getFirstName());
    order.setRecipientLastName(account.getPerson().getLastName());
    order.setDeliveryAddress(address);
    order.setTotalPrice(totalPrice);
    order.setAccount(account);
    order.setOrderedProducts(orderedProducts);
    order.setOrderState(OrderState.CREATED);
    return orderFacade.create(order);
  }

  @Override
  @RolesAllowed(CLIENT)
  public Order createWithGivenShippingData(Order order, String login, Map<Long, Integer> orderedProductsMap) {
    Double totalPrice = 0.0;
    List<OrderedProduct> orderedProducts = new ArrayList<>();

    for (Long productId : orderedProductsMap.keySet()) {
      Product product = productService.find(productId)
          .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

      if (orderedProductsMap.get(productId) > product.getAmount()) {
        throw ApplicationExceptionFactory.createInvalidProductAmountException();
      }

      OrderedProduct orderedProduct = OrderedProduct.builder()
          .amount(orderedProductsMap.get(productId))
          .price(product.getPrice())
          .product(product)
          .order(order)
          .build();
      orderedProducts.add(orderedProduct);
      totalPrice += orderedProduct.getPrice() * orderedProductsMap.get(productId);

      //TODO edit products amount in database
      product.setAmount(product.getAmount() - orderedProduct.getAmount());
      productFacade.update(product);
      //TODO make impossible to create order for employee that changed these products recently
    }
    Account account = accountService.getAccountByLogin(login)
        .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    order.setTotalPrice(totalPrice);
    order.setAccount(account);
    order.setOrderedProducts(orderedProducts);
    order.setOrderState(OrderState.CREATED);
    return orderFacade.create(order);
  }

  @Override
  public Order archive(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Order update(Long id, Order entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public Optional<Order> find(Long id) {
    return orderFacade.find(id);
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<Order> findAll() {
    return orderFacade.findAll();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<Order> findAllPresent() {
    return orderFacade.findAllPresent();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<Order> findAllArchived() {
    return orderFacade.findAllArchived();
  }

  @Override
  @RolesAllowed(CLIENT)
  public Order cancelOrder(Long id, String hash, String login) {
    List<Order> clientOrders = findByAccountLogin(login);
    Order order = Order.builder().build();
    for (Order clientOrder : clientOrders) {
      if (clientOrder.getId().equals(id)) {
        order = clientOrder;
      }
    }

    if (order == null) {
      throw ApplicationExceptionFactory.createOrderNotFoundException();
    }

    if (order.getOrderState().equals(OrderState.IN_DELIVERY)) {
      throw ApplicationExceptionFactory.createOrderAlreadyInDeliveryException();
    }

    if (order.getOrderState().equals(OrderState.DELIVERED)) {
      throw ApplicationExceptionFactory.createOrderAlreadyDeliveredException();
    }

    if (order.getOrderState().equals(CANCELLED)) {
      throw ApplicationExceptionFactory.createOrderAlreadyCancelledException();
    }

    if (!CryptHashUtils.verifyVersion(order.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    order.setOrderState(CANCELLED);
    return orderFacade.update(order);
  }


  @Override
  @RolesAllowed(EMPLOYEE)
  public Order cancelOrderAsEmployee(Long id, String hash) {
    Order order = find(id).orElseThrow(ApplicationExceptionFactory::createOrderNotFoundException);

    if (order.getOrderState().equals(OrderState.IN_DELIVERY)) {
      throw ApplicationExceptionFactory.createOrderAlreadyInDeliveryException();
    } else if (order.getOrderState().equals(OrderState.DELIVERED)) {
      throw ApplicationExceptionFactory.createOrderAlreadyDeliveredException();
    } else if (order.getOrderState().equals(CANCELLED)) {
      throw ApplicationExceptionFactory.createOrderAlreadyCancelledException();
    }

    if (!CryptHashUtils.verifyVersion(order.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    order.setOrderState(CANCELLED);
    return orderFacade.update(order);
  }

  @Override
  @RolesAllowed(CLIENT)
  public Order observeOrder(Long id, String hash, String login) {
    List<Order> orders = findByAccountLogin(login);
    Order clientOrder = Order.builder().build();
    for (Order order : orders) {
      if (order.getId().equals(id)) {
        clientOrder = order;
        break;
      }
    }

    if (clientOrder == null) {
      throw ApplicationExceptionFactory.createOrderNotFoundException();
    }

    if (clientOrder.getObserved()) {
      throw ApplicationExceptionFactory.createOrderAlreadyObservedException();
    }

    if (!CryptHashUtils.verifyVersion(clientOrder.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    clientOrder.setObserved(true);
    return orderFacade.update(clientOrder);
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public Order changeOrderState(Long id, OrderState state, String hash) {
    Order order = orderFacade.find(id).orElseThrow(ApplicationExceptionFactory::createOrderNotFoundException);
    if (state.ordinal() <= order.getOrderState().ordinal() || state.equals(CANCELLED)) {
      throw ApplicationExceptionFactory.createInvalidOrderStateTransitionException();
    }
    if (!CryptHashUtils.verifyVersion(order.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }
    String oldOrderState = order.getOrderState().name();
    order.setOrderState(state);
    String newOrderState = order.getOrderState().name();
    StringBuilder orderedProducts = new StringBuilder();
    for (OrderedProduct orderedProduct : order.getOrderedProducts()) {
      orderedProducts.append(orderedProduct.getProduct().getProductGroup().getName()).append("\n");
    }
    order = orderFacade.update(order);

    if (order.getObserved()) {
      mailService.sendEmailAboutOrderStateChange(order.getAccount().getEmail(), order.getAccount().getLocale(),
          String.valueOf(orderedProducts), oldOrderState, newOrderState);
    }
    return order;
  }

  @Override
  public void generateReport() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Order> findWithFilters(Double orderPrice, Integer orderSize, boolean isCompany) {
    throw new UnsupportedOperationException();
  }

}
