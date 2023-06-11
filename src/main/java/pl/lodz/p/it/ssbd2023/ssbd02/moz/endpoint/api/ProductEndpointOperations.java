package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.*;

@Local
public interface ProductEndpointOperations {
  ProductDto createProductWithNewImage(ProductCreateDto entity, byte[] image, String fileName);

  ProductDto createProductWithExistingImage(ProductCreateWithImageDto entity);

  Product archive(Long id);

  ProductDto update(Long id, UpdateProductDto entity);

  ProductDto find(Long id);

  List<ProductDto> findAll();

  List<ProductDto> findAllPresent();

  List<ProductDto> findAllArchived();

  List<ProductDto> findAllByWoodType(WoodType woodType);

  List<ProductDto> findAllByColor(Color color);

  List<ProductDto> findAllAvailable();

  List<ProductDto> findAllByPrice(Double minPrice, Double maxPrice);

  List<ProductDto> findAllByProductGroupColorAndWoodType(Long productGroupId, String color, String woodType);

  List<ProductDto> findAllByProductGroupId(Long productGroupId);

  List<ProductDto> findAllByCategoryId(Long categoryId);

  EditProductDto editProduct(Long id, EditProductDto editProductDto);

  Product deArchive(Long productId);

  List<OrderProductDto> findAllProductsBelongingToAccount(String login);
}
