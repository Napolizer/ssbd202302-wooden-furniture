package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupDto;

@Local
public interface ProductGroupEndpointOperations {

  ProductGroupDto create(ProductGroupDto entity);

  ProductGroupDto archive(Long id);

  ProductGroupDto update(Long id, ProductGroup entity);

  Optional<ProductGroupDto> find(Long id);

  List<ProductGroupDto> findAll();

  List<ProductGroupDto> findAllPresent();

  List<ProductGroupDto> findAllArchived();
}
