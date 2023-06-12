package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;


@Local
public interface ProductServiceOperations {
  Product createProductWithNewImage(Product entity, byte[] image, Long productGroupId, String fileName);

  Product createProductWithExistingImage(Product entity, Long productGroupId, Long imageProductId);

  Product archive(Long id);

  Product update(Long id, Product entity);

  Optional<Product> find(Long id);

  List<Product> findAll();

  List<Product> findAllPresent();

  List<Product> findAllArchived();

  List<Product> findAllByWoodType(WoodType woodType);

  List<Product> findAllByColor(Color color);

  List<Product> findAllAvailable();

  List<Product> findAllByPrice(Double minPrice, Double maxPrice);

  List<Product> findAllByProductGroupColorAndWoodType(Long productGroupId, Color color, WoodType woodType);

  boolean isLastTransactionRollback();

  List<Product> findAllByProductGroup(Long productGroupId);

  List<Product> findAllByCategory(Long categoryId);

  Product editProduct(Long id, Product productWithChanges, String hash);

  Product deArchive(Long productId);
}
