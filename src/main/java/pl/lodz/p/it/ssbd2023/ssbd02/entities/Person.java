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
        @NamedQuery(name = Person.FIND_BY_ACCOUNT_LOGIN,
                query = "SELECT person FROM Person person WHERE person.account.login = :accountLogin"),
        @NamedQuery(name = Person.FIND_ALL_BY_ADDRESS_ID,
                query = "SELECT person FROM Person person WHERE person.address.id = :addressId"),
        @NamedQuery(name = Person.FIND_BY_ACCOUNT_ID,
                query = "SELECT person FROM Person person WHERE person.account.id = :accountId")
})

public class Person extends AbstractEntity {
    public static final String FIND_ALL_BY_FIRST_NAME = "Person.findAllByFirstName";
    public static final String FIND_ALL_BY_LAST_NAME = "Person.findAllByLastName";
    public static final String FIND_BY_ACCOUNT_LOGIN = "Person.findByAccountLogin";
    public static final String FIND_ALL_BY_ADDRESS_ID = "Person.findAllByAddressId";
    public static final String FIND_BY_ACCOUNT_ID = "Person.findByAccountId";

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    private Address address;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "account_id", unique = true)
    private Account account;

}
