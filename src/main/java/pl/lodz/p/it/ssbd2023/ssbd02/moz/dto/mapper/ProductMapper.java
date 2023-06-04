package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import jakarta.ejb.Stateless;
import java.util.Base64;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.CategoryDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupInfoDto;

@Stateless
public class ProductMapper {


  public ProductDto mapToProductDto(Product product) {
    if (product.getImage() != null) {
      return ProductDto.builder()
              .id(product.getId())
              .price(product.getPrice())
              .available(product.getAvailable())
              .image(Base64.getEncoder().encodeToString(product.getImage()))
              .weight(product.getWeight())
              .amount(product.getAmount())
              .weightInPackage(product.getWeightInPackage())
              .furnitureDimensions(product.getFurnitureDimensions())
              .packageDimensions(product.getPackageDimensions())
              .color(product.getColor())
              .woodType(product.getWoodType())
              .productGroup(mapToProductGroupInfoDto(product.getProductGroup())
              )
              .build();
    } else {
      return ProductDto.builder()
              .id(product.getId())
              .price(product.getPrice())
              .available(product.getAvailable())
              .weight(product.getWeight())
              .amount(product.getAmount())
              .weightInPackage(product.getWeightInPackage())
              .furnitureDimensions(product.getFurnitureDimensions())
              .packageDimensions(product.getPackageDimensions())
              .color(product.getColor())
              .woodType(product.getWoodType())
              .productGroup(mapToProductGroupInfoDto(product.getProductGroup())
              )
              .build();
    }
  }

  public ProductGroupInfoDto mapToProductGroupInfoDto(ProductGroup productGroup) {
    return ProductGroupInfoDto.builder()
            .id(productGroup.getId())
            .name(productGroup.getName())
            .averageRating(productGroup.getAverageRating())
            .category(mapToCategoryDto(productGroup.getCategory()))
            .archive(productGroup.getArchive())
            .build();
  }

  public CategoryDto mapToCategoryDto(Category category) {
    return CategoryDto.builder()
            .id(category.getId())
            .name(category.getCategoryName())
            .build();
  }
}
