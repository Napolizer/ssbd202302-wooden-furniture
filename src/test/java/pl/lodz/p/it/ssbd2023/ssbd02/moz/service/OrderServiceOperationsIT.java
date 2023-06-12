package pl.lodz.p.it.ssbd2023.ssbd02.moz.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
import java.util.Optional;
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
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.mok.ApplicationOptimisticLockException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz.InvalidOrderStateTransitionException;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.moz.OrderNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd02.factories.OrderFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.OrderServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@ExtendWith(ArquillianExtension.class)
public class OrderServiceOperationsIT {
  @Resource
  private UserTransaction utx;
  @Inject
  private AdminAuth admin;
  @Inject
  private EmployeeAuth employee;
  @Inject
  private SalesRepAuth salesRep;
  @Inject
  private ClientAuth client;
  @Inject
  private OrderServiceOperations service;
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
        .addPackages(true, "at.favre.lib")
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
  void shouldProperlyChangeOrderState() throws Exception {
    utx.begin();
    Order order = orderFactory.create("user123");
    utx.commit();

    employee.call(() -> {
      Optional<Order> orderOptional = service.find(order.getId());
      assertThat(orderOptional.isPresent(), equalTo(true));
      assertThat(orderOptional.get().getOrderState(), equalTo(CREATED));

      Order updatedOrder = service.changeOrderState(order.getId(), COMPLETED, CryptHashUtils.hashVersion(order.getSumOfVersions()));
      assertThat(updatedOrder.getOrderState(), equalTo(COMPLETED));
      orderOptional = service.find(order.getId());
      assertThat(orderOptional.isPresent(), equalTo(true));
      assertThat(orderOptional.get().getOrderState(), equalTo(COMPLETED));

      updatedOrder = service.changeOrderState(order.getId(), DELIVERED, CryptHashUtils.hashVersion(orderOptional.get().getSumOfVersions()));
      assertThat(updatedOrder.getOrderState(), equalTo(DELIVERED));
      orderOptional = service.find(order.getId());
      assertThat(orderOptional.isPresent(), equalTo(true));
      assertThat(orderOptional.get().getOrderState(), equalTo(DELIVERED));
    });
  }

  @Test
  void shouldFailToChangeOrderStateToTheSameOne() throws Exception {
    utx.begin();
    Order order = orderFactory.create("user123");
    utx.commit();

    employee.call(() -> {
      Optional<Order> orderOptional = service.find(order.getId());
      assertThat(orderOptional.isPresent(), equalTo(true));
      assertThat(orderOptional.get().getOrderState(), equalTo(CREATED));

      assertThrows(InvalidOrderStateTransitionException.class, () -> {
        service.changeOrderState(order.getId(), CREATED, CryptHashUtils.hashVersion(order.getSumOfVersions()));
      });
    });
  }

  @Test
  void shouldFailToChangeOrderStateToInvalidOne() throws Exception {
    utx.begin();
    Order order = orderFactory.create("user123");
    utx.commit();

    employee.call(() -> {
      Order updatedOrder = service.changeOrderState(order.getId(), DELIVERED, CryptHashUtils.hashVersion(order.getSumOfVersions()));

      List<OrderState> invalidStates = List.of(CREATED, COMPLETED, IN_DELIVERY, DELIVERED);

      for (OrderState invalidState : invalidStates) {
        assertThrows(InvalidOrderStateTransitionException.class, () -> {
          service.changeOrderState(order.getId(), invalidState, CryptHashUtils.hashVersion(updatedOrder.getSumOfVersions()));
        });
      }
    });
  }

  @Test
  void shouldFailToChangeOrderStateWithInvalidHash() throws Exception {
    utx.begin();
    Order order = orderFactory.create("user123");
    utx.commit();

    employee.call(() -> {
      assertThrows(ApplicationOptimisticLockException.class, () -> {
        service.changeOrderState(order.getId(), COMPLETED, "invalidHash");
      });
    });
  }

  @Test
  void shouldFailToChangeOrderStateOfNonExistingOrder() {
    employee.call(() -> {
      assertThrows(OrderNotFoundException.class, () -> {
        service.changeOrderState(0L, COMPLETED, "hash");
      });
    });
  }

  @Test
  void shouldFailToChangeOrderStateAsNonEmployee() throws Exception {
    utx.begin();
    Order order = orderFactory.create("user123");
    utx.commit();

    admin.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        service.changeOrderState(1L, IN_DELIVERY, CryptHashUtils.hashVersion(order.getSumOfVersions()));
      });
    });
    salesRep.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        service.changeOrderState(1L, IN_DELIVERY, CryptHashUtils.hashVersion(order.getSumOfVersions()));
      });
    });
    client.call(() -> {
      assertThrows(EJBAccessException.class, () -> {
        service.changeOrderState(1L, IN_DELIVERY, CryptHashUtils.hashVersion(order.getSumOfVersions()));
      });
    });
  }

  @Test
  void shouldFailToChangeOrderStateAsGuest() {
    assertThrows(EJBAccessException.class, () -> {
      service.changeOrderState(1L, IN_DELIVERY, "hash");
    });
  }

  @Test
  void shouldFailToChangeStateToCancelled() throws Exception {
    utx.begin();
    Order order = orderFactory.create("user123");
    utx.commit();

    employee.call(() -> {
      Optional<Order> orderOptional = service.find(order.getId());
      assertThat(orderOptional.isPresent(), equalTo(true));
      assertThat(orderOptional.get().getOrderState(), equalTo(CREATED));

      assertThrows(InvalidOrderStateTransitionException.class, () -> {
        service.changeOrderState(order.getId(), CANCELLED, CryptHashUtils.hashVersion(order.getSumOfVersions()));
      });
    });
  }
}
