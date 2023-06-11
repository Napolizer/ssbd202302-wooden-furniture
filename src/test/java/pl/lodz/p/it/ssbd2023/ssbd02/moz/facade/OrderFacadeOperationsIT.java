package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.CANCELLED;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.COMPLETED;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.CREATED;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.DELIVERED;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.IN_DELIVERY;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBAccessException;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import java.io.File;
import java.util.List;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth.AdminAuth;
import pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth.ClientAuth;
import pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth.EmployeeAuth;
import pl.lodz.p.it.ssbd2023.ssbd02.arquillian.auth.SalesRepAuth;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.factories.OrderFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.OrderFacadeOperations;

@ExtendWith(ArquillianExtension.class)
public class OrderFacadeOperationsIT {
  @Resource
  private UserTransaction utx;
  @Inject
  private AdminAuth administrator;
  @Inject
  private EmployeeAuth employee;
  @Inject
  private SalesRepAuth salesRep;
  @Inject
  private ClientAuth client;
  @Inject
  private OrderFacadeOperations facade;
  @Inject
  private OrderFactory orderFactory;

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class)
        .addPackages(true, "pl.lodz.p.it.ssbd2023.ssbd02")
        .addPackages(true, "org.postgresql")
        .addPackages(true, "org.hamcrest")
        .addPackages(true, "io.jsonwebtoken")
        .addPackages(true, "org.apache")
        .addPackages(true, "com.google.cloud")
        .addPackages(true, "com.google.auth")
        .addAsResource(new File("src/test/resources/"), "")
        .addAsWebInfResource(new File("src/test/resources/WEB-INF/glassfish-web.xml"), "glassfish-web.xml");
  }

  @AfterEach
  public void teardown() throws Exception {
    utx.begin();
    orderFactory.clean();
    utx.commit();
  }

  @Test
  void properlyGetsAllOrders() throws Exception {
    employee.call(() ->{
      List<Order> orderList = facade.findAll();
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(0));
    });

    utx.begin();
    orderFactory.create("user1");
    utx.commit();

    employee.call(() -> {
      List<Order> orderList = facade.findAll();
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(1));
      Order order = orderList.get(0);
      assertThat(order.getId(), notNullValue());
      assertThat(order.getOrderState(), equalTo(order.getOrderState()));
      assertThat(order.getOrderedProducts().size(), equalTo(1));
      assertThat(order.getRecipientFirstName(), notNullValue());
      assertThat(order.getRecipientLastName(), notNullValue());
      assertThat(order.getDeliveryAddress(), notNullValue());
      assertThat(order.getAccount(), notNullValue());
      assertThat(order.getOrderedProducts(), notNullValue());
      assertThat(order.getOrderedProducts().size(), equalTo(1));
      assertThat(order.getObserved(), equalTo(false));
    });

    utx.begin();
    orderFactory.create("user2");
    orderFactory.create("user3");
    orderFactory.create("user4");
    orderFactory.create("user5");
    utx.commit();

    employee.call(() -> {
      List<Order> orderList = facade.findAll();
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(5));
    });
  }

  @Test
  void failsToGetAllOrdersFromAccountsWithoutEmployeeRole() {
    administrator.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        facade.findAll();
      });
    });
    salesRep.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        facade.findAll();
      });
    });
    client.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        facade.findAll();
      });
    });
  }

  @Test
  void failsToGetAllOrdersAsGuest() {
    assertThrows(EJBAccessException.class, () -> {
      facade.findAll();
    });
  }

  @Test
  void properlyGetsAllPresentOrders() throws Exception {
    employee.call(() ->{
      List<Order> orderList = facade.findAllPresent();
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(0));
    });

    utx.begin();
    orderFactory.create("user1");
    utx.commit();

    employee.call(() -> {
      List<Order> orderList = facade.findAllPresent();
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(1));
      Order order = orderList.get(0);
      assertThat(order.getId(), notNullValue());
      assertThat(order.getOrderState(), equalTo(order.getOrderState()));
      assertThat(order.getOrderedProducts().size(), equalTo(1));
      assertThat(order.getRecipientFirstName(), notNullValue());
      assertThat(order.getRecipientLastName(), notNullValue());
      assertThat(order.getDeliveryAddress(), notNullValue());
      assertThat(order.getAccount(), notNullValue());
      assertThat(order.getOrderedProducts(), notNullValue());
      assertThat(order.getOrderedProducts().size(), equalTo(1));
      assertThat(order.getObserved(), equalTo(false));
    });

    utx.begin();
    orderFactory.create("user2");
    orderFactory.create("user3");
    orderFactory.create("user4");
    orderFactory.create("user5");
    utx.commit();

    employee.call(() -> {
      List<Order> orderList = facade.findAllPresent();
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(5));
    });
  }

  @Test
  void failsToGetAllPresentOrdersFromAccountsWithoutEmployeeRole() {
    administrator.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        facade.findAllPresent();
      });
    });
    salesRep.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        facade.findAllPresent();
      });
    });
    client.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        facade.findAllPresent();
      });
    });
  }

  @Test
  void failsToGetAllPresentOrdersAsGuest() {
    assertThrows(EJBAccessException.class, () -> {
      facade.findAllPresent();
    });
  }

  @Test
  void properlyGetsAllArchivedOrders() throws Exception {
    employee.call(() ->{
      List<Order> orderList = facade.findAllPresent();
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(0));
    });

    utx.begin();
    orderFactory.create("user1", true);
    utx.commit();

    employee.call(() -> {
      List<Order> orderList = facade.findAllArchived();
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(1));
      Order order = orderList.get(0);
      assertThat(order.getId(), notNullValue());
      assertThat(order.getArchive(), equalTo(true));
      assertThat(order.getOrderState(), equalTo(order.getOrderState()));
      assertThat(order.getOrderedProducts().size(), equalTo(1));
      assertThat(order.getRecipientFirstName(), notNullValue());
      assertThat(order.getRecipientLastName(), notNullValue());
      assertThat(order.getDeliveryAddress(), notNullValue());
      assertThat(order.getAccount(), notNullValue());
      assertThat(order.getOrderedProducts(), notNullValue());
      assertThat(order.getOrderedProducts().size(), equalTo(1));
      assertThat(order.getObserved(), equalTo(false));
    });

    utx.begin();
    orderFactory.create("user2");
    orderFactory.create("user3");
    orderFactory.create("user4");
    orderFactory.create("user5");
    utx.commit();

    employee.call(() -> {
      List<Order> orderList = facade.findAllArchived();
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(1));
    });
  }

  @Test
  void failsToGetAllArchivedOrdersFromAccountsWithoutEmployeeRole() {
    administrator.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        facade.findAllArchived();
      });
    });
    salesRep.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        facade.findAllArchived();
      });
    });
    client.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        facade.findAllArchived();
      });
    });
  }

  @Test
  void failsToGetAllArchivedOrdersAsGuest() {
    assertThrows(EJBAccessException.class, () -> {
      facade.findAllArchived();
    });
  }

  @Test
  void properlyFindsByState() throws Exception {
    employee.call(() -> {
      for (OrderState state : OrderState.values()) {
        List<Order> orderList = facade.findByState(state);
        assertThat(orderList, notNullValue());
        assertThat(orderList.size(), equalTo(0));
      }
    });

    utx.begin();
    orderFactory.create("user1", true);
    orderFactory.create("user2", true);
    utx.commit();

    employee.call(() -> {
      List<Order> orderList = facade.findByState(CREATED);
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(2));
      Order order = orderList.get(0);
      assertThat(order.getId(), notNullValue());
      assertThat(order.getArchive(), equalTo(true));
      assertThat(order.getOrderState(), equalTo(order.getOrderState()));
      assertThat(order.getOrderedProducts().size(), equalTo(1));
      assertThat(order.getRecipientFirstName(), notNullValue());
      assertThat(order.getRecipientLastName(), notNullValue());
      assertThat(order.getDeliveryAddress(), notNullValue());
      assertThat(order.getAccount(), notNullValue());
      assertThat(order.getOrderedProducts(), notNullValue());
      assertThat(order.getOrderedProducts().size(), equalTo(1));
      assertThat(order.getObserved(), equalTo(false));

      orderList = facade.findByState(COMPLETED);
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(0));

      orderList = facade.findByState(IN_DELIVERY);
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(0));

      orderList = facade.findByState(DELIVERED);
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(0));

      orderList = facade.findByState(CANCELLED);
      assertThat(orderList, notNullValue());
      assertThat(orderList.size(), equalTo(0));
    });
  }

  @Test
  void failsToGetByStateOrdersFromAccountsWithoutEmployeeRole() {
    administrator.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        for (OrderState state : OrderState.values()) {
          facade.findByState(state);
        }
      });
    });
    salesRep.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        for (OrderState state : OrderState.values()) {
          facade.findByState(state);
        }
      });
    });
    client.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        for (OrderState state : OrderState.values()) {
          facade.findByState(state);
        }
      });
    });
  }

  @Test
  void failsToGetByStateOrdersAsGuest() {
    assertThrows(EJBAccessException.class, () -> {
      for (OrderState state : OrderState.values()) {
        facade.findByState(state);
      }
    });
  }
}
