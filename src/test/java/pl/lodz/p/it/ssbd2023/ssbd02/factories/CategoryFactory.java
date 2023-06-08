package pl.lodz.p.it.ssbd2023.ssbd02.factories;

import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.CategoryName.BED;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Image;

@Stateless
public class CategoryFactory {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;

  public Category create() throws Exception {
    Category category = Category.builder()
        .categoryName(BED)
        .image(new Image("https://www.ssbd.com/image.jpg"))
        .build();
    em.persist(category);
    return category;
  }

  public void clean() throws Exception {
    em.createQuery("DELETE FROM Category").executeUpdate();
  }
}
