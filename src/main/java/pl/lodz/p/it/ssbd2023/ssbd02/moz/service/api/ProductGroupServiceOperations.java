package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Rate;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.CategoryName;

@Local
public interface ProductGroupServiceOperations {

  boolean isLastTransactionRollback();

  ProductGroup create(ProductGroup entity, CategoryName categoryName);

  ProductGroup archive(Long id, String hash);

  ProductGroup editProductGroupName(Long id, String name, String hash);

  Optional<ProductGroup> find(Long id);

  List<ProductGroup> findAll();

  List<ProductGroup> findAllPresent();

  List<ProductGroup> findAllArchived();

  Rate rateProductGroup(String login, Integer rateValue, Long productGroupId);

  Rate changeRateOnProductGroup(String login, Integer rateValue, Long productGroupId);

  void removeRateFromProductGroup(String login, Long productGroupId);
}
