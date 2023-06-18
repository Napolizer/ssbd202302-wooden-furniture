package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api;

import jakarta.ejb.Local;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;


@Local
public interface CategoryServiceOperations {

  boolean isLastTransactionRollback();

  List<Category> findAllParentCategories();
}
