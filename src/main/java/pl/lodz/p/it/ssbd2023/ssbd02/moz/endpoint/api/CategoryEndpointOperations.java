package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.category.CategoryDto;

@Local
public interface CategoryEndpointOperations {

  Optional<Category> find(Long id);

  List<CategoryDto> findAllParentCategories();

  List<Category> findAll();

  List<Category> findAllPresent();

  List<Category> findAllArchived();
}
