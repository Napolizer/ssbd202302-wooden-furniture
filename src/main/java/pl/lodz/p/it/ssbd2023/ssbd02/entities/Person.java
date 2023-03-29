package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@NamedQueries({
        @NamedQuery(name = Person.FIND_ALL_BY_FIRST_NAME,
                query = "SELECT person FROM Person person WHERE person.firstName = :firstName"),
        @NamedQuery(name = Person.FIND_ALL_BY_LAST_NAME,
                query = "SELECT person FROM Person person WHERE person.lastName = :lastName"),
        @NamedQuery(name = Person.FIND_BY_COMPANY_NIP,
                query = "SELECT person FROM Person person WHERE person.company.nip = :companyNip"),
        @NamedQuery(name = Person.FIND_BY_ACCOUNT_LOGIN,
                query = "SELECT person FROM Person person WHERE person.account.login = :accountLogin"),
        @NamedQuery(name = Person.FIND_ALL_BY_ADDRESS_ID,
                query = "SELECT person FROM Person person WHERE person.address.id = :addressId"),
        @NamedQuery(name = Person.FIND_BY_ACCOUNT_ID,
                query = "SELECT person FROM Person person WHERE person.account.id = :accountId"),
        @NamedQuery(name = Person.FIND_BY_COMPANY_ID,
                query = "SELECT person FROM Person person WHERE person.company.id = :companyId")
})

public class Person extends AbstractEntity {
    public static final String FIND_ALL_BY_FIRST_NAME = "Person.findAllByFirstName";
    public static final String FIND_ALL_BY_LAST_NAME = "Person.findAllByLastName";
    public static final String FIND_BY_COMPANY_NIP = "Person.findByCompanyNIP";
    public static final String FIND_BY_ACCOUNT_LOGIN = "Person.findByAccountLogin";
    public static final String FIND_ALL_BY_ADDRESS_ID = "Person.findAllByAddressId";
    public static final String FIND_BY_ACCOUNT_ID = "Person.findByAccountId";
    public static final String FIND_BY_COMPANY_ID = "Person.findByCompanyId";

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "adress_id", nullable = false)
    private Address address;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "account_id")
    private Account account;

}
