package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.CategoryName;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.category.CategoryDto;

public final class CategoryMapper {
  public static CategoryDto mapToCategoryDto(Category category) {
    CategoryDto categoryDto = CategoryDto.builder()
            .id(category.getId())
            .name(category.getCategoryName())
            .parentName(category.getParentCategory() != null ? category.getParentCategory().getCategoryName() : null)
            .build();
    category.getSubcategories().forEach(sub -> categoryDto.getSubcategories().add(mapToCategoryDto(sub)));
    return categoryDto;
  }

  public static CategoryName mapToCategoryName(String categoryName) {
    try {
      return CategoryName.valueOf(categoryName.toUpperCase());
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createCategoryNotFoundException();
    }
  }
}
