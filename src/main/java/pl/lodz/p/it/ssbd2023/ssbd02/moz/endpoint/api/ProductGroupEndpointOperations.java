package pl.lodz.p.it.ssbd2023.ssbd02.moz.endpoint.api;

import jakarta.ejb.Local;
import java.util.List;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.EditProductGroupDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupArchiveDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupCreateDto;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.dto.product.ProductGroupInfoDto;

@Local
public interface ProductGroupEndpointOperations {

  ProductGroupInfoDto create(ProductGroupCreateDto entity);

  ProductGroupInfoDto archive(Long id, ProductGroupArchiveDto productGroupArchiveDto);

  ProductGroupInfoDto editProductGroupName(Long id, EditProductGroupDto editProductGroupDto);

  ProductGroupInfoDto find(Long id);

  List<ProductGroupInfoDto> findAll();

  List<ProductGroupInfoDto> findAllPresent();

  List<ProductGroupInfoDto> findAllArchived();
}
