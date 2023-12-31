package pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.mapper;

import jakarta.ejb.Stateful;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Dimensions;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Image;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductHistory;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.EditProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductHistoryDto;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;

@Stateful
public class ProductMapper {


  public ProductDto mapToProductDto(Product product) {
    return ProductDto.builder()
              .id(product.getId())
              .price(product.getPrice())
              .archive(product.getArchive())
              .imageUrl(product.getImage().getUrl())
              .weight(product.getWeight())
              .amount(product.getAmount())
              .weightInPackage(product.getWeightInPackage())
              .furnitureDimensions(product.getFurnitureDimensions())
              .packageDimensions(product.getPackageDimensions())
              .color(product.getColor())
              .woodType(product.getWoodType())
              .productGroup(ProductGroupMapper.mapToProductGroupInfoWithoutHashDto(product.getProductGroup()))
              .build();
  }

  public ProductDto mapToSingleProductDto(Product product) {
    return ProductDto.builder()
            .id(product.getId())
            .price(product.getPrice())
            .archive(product.getArchive())
            .imageUrl(product.getImage().getUrl())
            .weight(product.getWeight())
            .amount(product.getAmount())
            .weightInPackage(product.getWeightInPackage())
            .furnitureDimensions(product.getFurnitureDimensions())
            .packageDimensions(product.getPackageDimensions())
            .color(product.getColor())
            .woodType(product.getWoodType())
            .hash(CryptHashUtils.hashVersion(product.getSumOfVersions()))
            .productGroup(ProductGroupMapper.mapToProductGroupInfoWithoutHashDto(product.getProductGroup()))
            .build();
  }

  public static Product mapEditProductDtoToProduct(EditProductDto editProductDto) {

    return Product.builder()
            .price(editProductDto.getPrice())
            .amount(editProductDto.getAmount())
            .build();
  }

  public Product mapToProduct(ProductDto productDto) {
    return Product.builder()
        .id(productDto.getId())
        .price(productDto.getPrice())
        .archive(productDto.getArchive())
        .weight(productDto.getWeight())
        .amount(productDto.getAmount())
        .weightInPackage(productDto.getWeightInPackage())
        .furnitureDimensions(productDto.getFurnitureDimensions())
        .packageDimensions(productDto.getPackageDimensions())
        .color(productDto.getColor())
        .woodType(productDto.getWoodType())
        .build();
  }

  public static Product mapToProduct(ProductCreateDto productCreateDto) {
    return Product.builder()
            .price(productCreateDto.getPrice())
            .archive(false)
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
    if (color == null || color.equals("")) {
      return null;
    }
    try {
      return Color.valueOf(color.toUpperCase());
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createInvalidColorException();
    }
  }

  public static WoodType mapToWoodType(String woodType) {
    if (woodType == null || woodType.equals("")) {
      return null;
    }
    try {
      return WoodType.valueOf(woodType.toUpperCase());
    } catch (Exception e) {
      throw ApplicationExceptionFactory.createInvalidWoodTypeException();
    }
  }


  public EditProductDto mapToEditProductDto(Product product) {
    return EditProductDto.builder()
            .price(product.getPrice())
            .amount(product.getAmount())
            .hash(CryptHashUtils.hashVersion(product.getSumOfVersions()))
            .build();
  }

  public static ProductHistoryDto mapToProductHistoryDto(ProductHistory productHistory) {
    return ProductHistoryDto.builder()
            .editDate(productHistory.getCreatedAt())
            .editedBy(productHistory.getCreatedBy().getPerson().getFirstName()
                    + " " + productHistory.getCreatedBy().getPerson().getLastName())
            .newValue(productHistory.getNewValue())
            .oldValue(productHistory.getOldValue())
            .fieldName(productHistory.getFieldName())
            .build();
  }
}
