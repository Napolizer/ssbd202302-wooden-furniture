package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Color;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.WoodType;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.CreateProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.UpdateProductDto;


@Local
public interface ProductEndpointOperations {
  ProductDto create(CreateProductDto entity);

  ProductDto archive(Long id, UpdateProductDto entity);

  ProductDto update(Long id, UpdateProductDto entity);

  Optional<ProductDto> find(Long id);

  List<ProductDto> findAll();

  List<ProductDto> findAllPresent();

  List<ProductDto> findAllArchived();

  List<ProductDto> findAllByWoodType(WoodType woodType);

  List<ProductDto> findAllByColor(Color color);

  List<ProductDto> findAllAvailable();

  List<ProductDto> findAllByPrice(Double minPrice, Double maxPrice);
}
