package pl.lodz.p.it.ssbd2023.ssbd02.factories;

import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color.GREEN;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType.OAK;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Dimensions;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Image;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;

@Stateless
public class ProductFactory {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;

  @Inject
  private ProductGroupFactory productGroupFactory;

  public Product create(String name) throws Exception {
    Product product = Product.builder()
        .price(100.0)
        .archive(false)
        .image(new Image("https://www.ssbd.com/image.jpg"))
        .weight(10.0)
        .amount(2)
        .weightInPackage(11.0)
        .furnitureDimensions(new Dimensions(10, 10, 10))
        .packageDimensions(new Dimensions(11, 11, 11))
        .color(GREEN)
        .woodType(OAK)
        .productGroup(productGroupFactory.create(name))
        .build();
    em.persist(product);
    return product;
  }

  public void clean() throws Exception {
    em.createQuery("DELETE FROM Product").executeUpdate();
    productGroupFactory.clean();
  }
}
