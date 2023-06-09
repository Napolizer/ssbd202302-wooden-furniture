package pl.lodz.p.it.ssbd2023.ssbd02.factories;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;

@Stateless
public class ProductGroupFactory {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;
  @Inject
  private CategoryFactory categoryFactory;

  public ProductGroup create(String name) throws Exception {
    ProductGroup productGroup = ProductGroup.builder()
        .name(name)
        .averageRating(0.0)
        .category(categoryFactory.create())
        .build();
    em.persist(productGroup);
    return productGroup;
  }

  public void clean() throws Exception {
    em.createQuery("DELETE FROM product_group").executeUpdate();
    categoryFactory.clean();
  }
}
