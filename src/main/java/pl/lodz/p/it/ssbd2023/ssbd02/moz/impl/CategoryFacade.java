package pl.lodz.p.it.ssbd2023.ssbd02.moz.impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.api.CategoryFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.sharedmod.facade.AbstractFacade;

@Stateless
public class CategoryFacade extends AbstractFacade<Category> implements CategoryFacadeOperations {
  @PersistenceContext(unitName = "ssbd02mozPU")
  private EntityManager em;

  public CategoryFacade() {
    super(Category.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  @Override
  public List<Category> findAllByParentCategory(Category parentCategory) {
    return em.createNamedQuery(Category.FIND_ALL_BY_PARENT_CATEGORY, Category.class)
        .setParameter("parentCategory", parentCategory)
        .getResultList();
  }
}
