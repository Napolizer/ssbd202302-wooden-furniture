package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupInfoDto;

public final class ProductGroupMapper {
  public static ProductGroupInfoDto mapToProductGroupInfoDto(ProductGroup productGroup) {
    return ProductGroupInfoDto.builder().id(productGroup.getId())
            .averageRating(productGroup.getAverageRating())
            .name(productGroup.getName())
            .archive(productGroup.getArchive())
            .category(CategoryMapper.mapToCategoryDto(productGroup.getCategory())).build();
  }

}
