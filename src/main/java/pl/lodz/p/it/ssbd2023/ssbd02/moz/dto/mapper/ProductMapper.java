package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import jakarta.ejb.Stateless;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Category;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Dimensions;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Image;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.EditProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupInfoDto;

@Stateless
public class ProductMapper {


  public ProductDto mapToProductDto(Product product) {
    if (product.getImage() != null) {
      return ProductDto.builder()
              .id(product.getId())
              .price(product.getPrice())
              .productState(product.getProductState())
              .imageUrl(product.getImage().getUrl())
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
              .productState(product.getProductState())
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
            .archive(productGroup.getArchive())
            .build();
  }

  public static Product mapEditProductDtoToProduct(EditProductDto editProductDto) {

    return Product.builder()
            .price(editProductDto.getPrice())
            .productState(editProductDto.getProductState())
            .weight(editProductDto.getWeight())
            .amount(editProductDto.getAmount())
            .weightInPackage(editProductDto.getWeightInPackage())
            .furnitureDimensions(editProductDto.getFurnitureDimensions())
            .packageDimensions(editProductDto.getPackageDimensions())
            .color(editProductDto.getColor())
            .woodType(editProductDto.getWoodType())
            .build();
  }

  public Product mapToProduct(ProductDto productDto) {
    return Product.builder()
        .id(productDto.getId())
        .price(productDto.getPrice())
        .productState(productDto.getProductState())
        .weight(productDto.getWeight())
        .amount(productDto.getAmount())
        .weightInPackage(productDto.getWeightInPackage())
        .furnitureDimensions(productDto.getFurnitureDimensions())
        .packageDimensions(productDto.getPackageDimensions())
        .color(productDto.getColor())
        .woodType(productDto.getWoodType())
        .build();
  }

  public Product mapToProduct(ProductCreateDto productCreateDto) {
    return Product.builder()
            .price(productCreateDto.getPrice())
            .productState(productCreateDto.getProductState())
            .weight(productCreateDto.getWeight())
            .amount(productCreateDto.getAmount())
            .weightInPackage(productCreateDto.getWeightInPackage())
            .furnitureDimensions(new Dimensions(productCreateDto.getFurnitureWidth(),
                    productCreateDto.getFurnitureHeight(),
                    productCreateDto.getFurnitureDepth()))
            .packageDimensions(new Dimensions(productCreateDto.getPackageWidth(),
                    productCreateDto.getPackageHeight(),
                    productCreateDto.getPackageDepth()))
            .color(mapToColor(productCreateDto.getColor()))
            .woodType(mapToWoodType(productCreateDto.getWoodType()))
            .image(new Image(""))
            .build();
  }

  public static Color mapToColor(String color) {
    try {
      return Color.valueOf(color.toUpperCase());
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createProductCreateDtoValidationException();
    }
  }

  public static WoodType mapToWoodType(String woodType) {
    try {
      return WoodType.valueOf(woodType.toUpperCase());
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createProductCreateDtoValidationException();
    }
  }


}
