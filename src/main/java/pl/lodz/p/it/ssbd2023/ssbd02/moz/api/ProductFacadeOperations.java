package pl.lodz.p.it.ssbd2023.ssbd02.moz.api;

import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.sharedmod.facade.Facade;

public interface ProductFacadeOperations extends Facade<Product> {
  List<Product> findAllByWoodType(WoodType woodType);

  List<Product> findAllByColor(Color color);

  List<Product> findAllAvailable();

  List<Product> findAllByPrice(Double minPrice, Double maxPrice);
}
