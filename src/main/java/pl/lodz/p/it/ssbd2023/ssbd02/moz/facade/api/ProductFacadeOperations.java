package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductHistory;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.facade.Facade;

@Local
public interface ProductFacadeOperations extends Facade<Product> {
  Optional<Product> findById(Long id);

  List<Product> findAllByProductGroupColorAndWoodType(Long productGroupId, Color color, WoodType woodType);

  List<Product> findAllByProductGroup(Long productGroupId);

  List<Product> findAllByCategory(Long categoryId);

  List<ProductHistory> findAllDiscountsByEmployeeOfProductInCurrentMonth(Long productId, Long accountId);
}
