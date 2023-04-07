package pl.lodz.p.it.ssbd2023.ssbd02.mok.facade.api;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.facade.Facade;

import java.util.List;
import java.util.Optional;

public interface PersonFacadeOperations extends Facade<Person> {
    List<Person> findAllByFirstName(String firstName);
    List<Person> findAllByLastName(String lastName);
//    Optional<Person> findByCompanyNIP(String companyNIP);
    Optional<Person> findByAccountLogin(String accountLogin);
    List<Person> findAllByAddressId(Long addressId);
    Optional<Person> findByAccountId(Long accountId);
//    Optional<Person> findByCompanyId(Long companyId);

}
