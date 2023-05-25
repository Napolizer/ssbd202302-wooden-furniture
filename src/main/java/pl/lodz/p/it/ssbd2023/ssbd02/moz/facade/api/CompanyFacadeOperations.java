package pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api;

import java.util.List;
import java.util.Optional;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Company;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.facade.Facade;

@Local
public interface CompanyFacadeOperations extends Facade<Company> {
  Optional<Company> findByNip(String nip);

  List<Company> findAllByCompanyName(String companyName);
}
