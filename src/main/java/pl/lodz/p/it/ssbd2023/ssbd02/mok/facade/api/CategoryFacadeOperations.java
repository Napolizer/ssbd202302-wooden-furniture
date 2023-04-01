package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.Facade;

import java.util.List;

public interface CategoryFacadeOperations extends Facade<Category> {
    List<Category> findAllByParentCategory(Category parentCategory);
}
