package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.impl;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api.CategoryFacadeOperations;

import java.util.List;

@Stateless
public class CategoryFacade extends AbstractFacade<Category> implements CategoryFacadeOperations {
    @PersistenceContext(unitName = "ssbd02mokPU")
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
