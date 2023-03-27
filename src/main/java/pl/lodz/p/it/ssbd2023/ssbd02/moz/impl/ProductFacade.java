package pl.lodz.p.it.ssbd2023.ssbd02.moz.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.api.ProductFacadeOperations;

import java.util.List;

public class ProductFacade extends AbstractFacade<Product> implements ProductFacadeOperations {

    @PersistenceContext(unitName = "ssbd02mozPU")
    private EntityManager em;

    public ProductFacade() {
        super(Product.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return null;
    }

    @Override
    public List<Product> findAllByWoodType(WoodType woodType) {
        return em.createNamedQuery(Product.FIND_ALL_BY_WOOD_TYPE, Product.class)
                .setParameter("woodType", woodType)
                .getResultList();
    }

    @Override
    public List<Product> findAllByColor(Color color) {
        return em.createNamedQuery(Product.FIND_ALL_BY_COLOR, Product.class)
                .setParameter("color", color)
                .getResultList();
    }

    @Override
    public List<Product> findAllAvailable() {
        return em.createNamedQuery(Product.FIND_ALL_AVAILABLE, Product.class)
                .getResultList();
    }

    @Override
    public List<Product> findAllByPrice(Double minPrice, Double maxPrice) {
        return em.createNamedQuery(Product.FIND_ALL_BY_PRICE, Product.class)
                .setParameter("minPrice", minPrice)
                .setParameter("maxPrice", maxPrice)
                .getResultList();
    }
}
