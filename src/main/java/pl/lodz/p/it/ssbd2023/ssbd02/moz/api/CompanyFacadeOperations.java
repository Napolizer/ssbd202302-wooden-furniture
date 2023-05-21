package pl.lodz.p.it.ssbd2023.ssbd02.moz.api;

import java.util.List;
import java.util.Optional;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Company;
import pl.lodz.p.it.ssbd2023.ssbd02.sharedmod.facade.Facade;

public interface CompanyFacadeOperations extends Facade<Company> {
  Optional<Company> findByNip(String nip);

  List<Company> findAllByCompanyName(String companyName);
}
