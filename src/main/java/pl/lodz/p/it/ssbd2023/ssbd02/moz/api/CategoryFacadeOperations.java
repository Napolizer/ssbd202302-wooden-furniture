package pl.lodz.p.it.ssbd2023.ssbd02.moz.api;

import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.sharedmod.facade.Facade;

public interface CategoryFacadeOperations extends Facade<Category> {
  List<Category> findAllByParentCategory(Category parentCategory);
}
