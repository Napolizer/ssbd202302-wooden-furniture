package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;


@Local
public interface CategoryServiceOperations {

  Optional<Category> find(Long id);

  List<Category> findAllParentCategories();

  List<Category> findAll();

  List<Category> findAllPresent();

  List<Category> findAllArchived();
}
