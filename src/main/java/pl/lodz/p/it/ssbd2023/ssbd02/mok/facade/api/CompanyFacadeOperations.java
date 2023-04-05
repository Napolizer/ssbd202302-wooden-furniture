package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Company;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.facade.Facade;

import java.util.List;
import java.util.Optional;

public interface CompanyFacadeOperations extends Facade<Company> {
    Optional<Company> findByNip(String nip);
    List<Company> findAllByCompanyName(String companyName);
}