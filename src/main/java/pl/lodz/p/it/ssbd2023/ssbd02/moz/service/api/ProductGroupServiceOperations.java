package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api;

import jakarta.ejb.Local;
import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;

@Local
public interface ProductGroupServiceOperations {

  ProductGroup create(ProductGroup entity);

  ProductGroup archive(Long id);

  ProductGroup update(Long id, ProductGroup entity);

  Optional<ProductGroup> find(Long id);

  List<ProductGroup> findAll();

  List<ProductGroup> findAllPresent();

  List<ProductGroup> findAllArchived();

}
