package pl.lodz.p.it.ssbd2023.ssbd02.moz.api;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.WoodType;

import java.util.List;

public interface ProductFacadeOperations {
    List<Product> findAllByWoodType(WoodType woodType);
    List<Product> findAllByColor(Color color);
    List<Product> findAllAvailable();
    List<Product> findAllByPrice(Double minPrice, Double maxPrice);
}
