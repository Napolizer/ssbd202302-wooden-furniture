package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api;

import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.facade.Facade;

public interface ProductFacadeOperations extends Facade<Product> {
  List<Product> findAllByWoodType(WoodType woodType);

  List<Product> findAllByColor(Color color);

  List<Product> findAllAvailable();

  List<Product> findAllByPrice(Double minPrice, Double maxPrice);
}
