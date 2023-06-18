package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.category.CategoryDto;

@Local
public interface CategoryEndpointOperations {
  List<CategoryDto> findAllParentCategories();
}
