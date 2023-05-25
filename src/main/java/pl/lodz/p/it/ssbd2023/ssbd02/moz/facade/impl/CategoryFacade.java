package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.impl;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.CategoryFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.facade.AbstractFacade;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
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
  public Category create(Category entity) {
    //Operation will be not supported for this facade
    throw new UnsupportedOperationException();
  }

  @Override
  public Category archive(Category entity) {
    //Operation will be not supported for this facade
    throw new UnsupportedOperationException();
  }

  @Override
  public Category update(Category entity) {
    //Operation will be not supported for this facade
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Category> findAllByParentCategory(Category parentCategory) {
    return em.createNamedQuery(Category.FIND_ALL_BY_PARENT_CATEGORY, Category.class)
        .setParameter("parentCategory", parentCategory)
        .getResultList();
  }
}
