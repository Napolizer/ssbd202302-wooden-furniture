package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import jakarta.ejb.Stateless;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;

@Stateless
public class ProductMapper {
  public ProductDto mapToProductDto(Product product) {
    return ProductDto.builder()
             .id(product.getId())
             .price(product.getPrice())
             .available(product.getAvailable())
             .image(product.getImage())
             .weight(product.getWeight())
             .amount(product.getAmount())
             .weightInPackage(product.getWeightInPackage())
             .furnitureDimensions(product.getFurnitureDimensions())
             .packageDimensions(product.getPackageDimensions())
             .color(product.getColor())
             .woodType(product.getWoodType())
             .build();
  }
}
