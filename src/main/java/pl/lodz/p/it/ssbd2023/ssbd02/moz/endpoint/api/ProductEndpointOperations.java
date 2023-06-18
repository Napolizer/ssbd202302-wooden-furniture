package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.EditProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.OrderProductWithRateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateWithImageDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;

@Local
public interface ProductEndpointOperations {
  ProductDto createProductWithNewImage(ProductCreateDto entity, byte[] image, String fileName);

  ProductDto createProductWithExistingImage(ProductCreateWithImageDto entity);

  Product archive(Long id);

  ProductDto find(Long id);

  List<ProductDto> findAll();

  List<ProductDto> findAllByProductGroupColorAndWoodType(Long productGroupId, String color, String woodType);

  List<ProductDto> findAllByProductGroupId(Long productGroupId);

  List<ProductDto> findAllByCategoryId(Long categoryId);

  EditProductDto editProduct(Long id, EditProductDto editProductDto);

  List<OrderProductWithRateDto> findAllProductsBelongingToAccount(String login);
}
