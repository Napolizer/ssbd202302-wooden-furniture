package pl.lodz.p.it.ssbd2023.ssbd02.factories;

import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.CREATED;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderProduct;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;

@Stateless
public class OrderFactory {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;
  @Inject
  private AccountFactory accountFactory;
  @Inject
  private ProductFactory productFactory;

  public Order create(String login) throws Exception {
    return create(login, false);
  }

  public Order create(String login, boolean archive) throws Exception {
    Account account = accountFactory.createClient(login);
    Order order = Order.builder()
        .archive(archive)
        .orderState(CREATED)
        .recipient(account.getPerson())
        .deliveryAddress(account.getPerson().getAddress())
        .account(account)
        .build();
    Product product = productFactory.create(login);
    order.setOrderedProducts(List.of(
            OrderProduct.builder()
                    .product(product)
                    .order(order)
                    .price(product.getPrice())
                    .amount(product.getAmount())
                    .build()));
    em.persist(order);
    return order;
  }

  public void clean() throws Exception {
    em.createQuery("DELETE FROM sales_order").executeUpdate();
    productFactory.clean();
    accountFactory.clean();
  }
}
