package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.CategoryName;

@Local
public interface CategoryFacadeOperations {

  Optional<Category> find(Long id);

  Optional<Category> findByCategoryName(CategoryName categoryName);

  List<Category> findAllParentCategories();

  List<Category> findAll();

  List<Category> findAllPresent();

  List<Category> findAllArchived();
}
