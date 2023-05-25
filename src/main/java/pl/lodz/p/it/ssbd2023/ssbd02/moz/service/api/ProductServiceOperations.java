package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.WoodType;
import java.util.List;
import java.util.Optional;

@Local
public interface ProductServiceOperations {
  Product create(Product entity);

  Product archive(Product entity);

  Product update(Product entity);

  Optional<Product> find(Long id);

  List<Product> findAll();

  List<Product> findAllPresent();

  List<Product> findAllArchived();

  List<Product> findAllByWoodType(WoodType woodType);

  List<Product> findAllByColor(Color color);

  List<Product> findAllAvailable();

  List<Product> findAllByPrice(Double minPrice, Double maxPrice);
}
