package pl.lodz.p.it.ssbd2023.ssbd02.mok.api;

import pl.lodz.p.it.ssbd2023.ssbd02.entities.Person;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.Facade;

import java.util.List;

public interface PersonFacadeOperations extends Facade<Person> {
    List<Person> findAllByFirstName(String firstName);
    List<Person> findAllByLastName(String lastName);
    List<Person> findAllByCompanyNIP(String companyNIP);
    List<Person> findAllByAccountLogin(String accountLogin);
    List<Person> findAllByAddressId(Long addressId);
    List<Person> findAllByAccountId(Long accountId);
    List<Person> findAllByCompanyId(Long companyId);

}
