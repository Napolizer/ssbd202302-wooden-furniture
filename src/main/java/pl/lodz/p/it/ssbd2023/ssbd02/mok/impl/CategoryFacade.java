package pl.lodz.p.it.ssbd2023.ssbd02.mok.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.api.CategoryFacadeOperations;

import java.util.List;

public class CategoryFacade extends AbstractFacade<Category> implements CategoryFacadeOperations {
    @PersistenceContext(unitName = "ssbd02mok")
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
