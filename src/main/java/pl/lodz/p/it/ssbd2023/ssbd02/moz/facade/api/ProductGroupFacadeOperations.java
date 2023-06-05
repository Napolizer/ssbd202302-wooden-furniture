package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api;

import jakarta.ejb.Local;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.ProductGroup;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.facade.Facade;

@Local
public interface ProductGroupFacadeOperations extends Facade<ProductGroup> {
  Optional<ProductGroup> findById(Long productId);
}
