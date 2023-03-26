package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@NamedQueries({
        @NamedQuery(name = Person.FIND_ALL_BY_FIRST_NAME,
                query = "SELECT person FROM Person person WHERE person.firstName = :firstName"),
        @NamedQuery(name = Person.FIND_ALL_BY_LAST_NAME,
                query = "SELECT person FROM Person person WHERE person.lastName = :lastName"),
        @NamedQuery(name = Person.FIND_ALL_BY_COMPANY_NIP,
                query = "SELECT person FROM Person person WHERE person.company.nip = :companyNip"),
        @NamedQuery(name = Person.FIND_ALL_BY_ACCOUNT_LOGIN,
                query = "SELECT person FROM Person person WHERE person.account.login = :accountLogin"),
        @NamedQuery(name = Person.FIND_ALL_BY_ADDRESS_ID,
                query = "SELECT person FROM Person person WHERE person.address.id = :addressId"),
        @NamedQuery(name = Person.FIND_ALL_BY_ACCOUNT_ID,
                query = "SELECT person FROM Person person WHERE person.account.id = :accountId"),
        @NamedQuery(name = Person.FIND_ALL_BY_COMPANY_ID,
                query = "SELECT person FROM Person person WHERE person.company.id = :companyId")
})

public class Person extends AbstractEntity {
    public static final String FIND_ALL_BY_FIRST_NAME = "Person.findAllByFirstName";
    public static final String FIND_ALL_BY_LAST_NAME = "Person.findAllByLastName";
    public static final String FIND_ALL_BY_COMPANY_NIP = "Person.findAllByCompanyNIP";
    public static final String FIND_ALL_BY_ACCOUNT_LOGIN = "Person.findAllByAccountLogin";
    public static final String FIND_ALL_BY_ADDRESS_ID = "Person.findAllByAddressId";
    public static final String FIND_ALL_BY_ACCOUNT_ID = "Person.findAllByAccountId";
    public static final String FIND_ALL_BY_COMPANY_ID = "Person.findAllByCompanyId";

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "adress_id", nullable = false)
    private Address address;

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

}
