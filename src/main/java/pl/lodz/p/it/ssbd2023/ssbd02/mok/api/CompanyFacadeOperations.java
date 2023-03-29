package pl.lodz.p.it.ssbd2023.ssbd02.mok.api;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Company;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.Facade;

import java.util.List;

public interface CompanyFacadeOperations extends Facade<Company> {
    Company findByNip(String nip);
    List<Company> findAllByCompanyName(String companyName);
}