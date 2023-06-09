package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.EditProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.UpdateProductDto;

@Local
public interface ProductEndpointOperations {
  ProductDto create(ProductCreateDto entity, byte[] image, String fileName);

  ProductDto archive(Long id, UpdateProductDto entity);

  ProductDto update(Long id, UpdateProductDto entity);

  ProductDto find(Long id);

  List<ProductDto> findAll();

  List<ProductDto> findAllPresent();

  List<ProductDto> findAllArchived();

  List<ProductDto> findAllByWoodType(WoodType woodType);

  List<ProductDto> findAllByColor(Color color);

  List<ProductDto> findAllAvailable();

  List<ProductDto> findAllByPrice(Double minPrice, Double maxPrice);
  ProductDto editProduct(Long id, EditProductDto editProductDto);
}
