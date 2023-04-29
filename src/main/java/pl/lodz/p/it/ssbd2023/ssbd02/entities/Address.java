package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@SuperBuilder
@NamedQueries({
    @NamedQuery(
        name = Address.FIND_ALL_BY_COUNTRY,
        query = "SELECT address FROM Address address WHERE address.country = :country"),
    @NamedQuery(
        name = Address.FIND_ALL_BY_CITY,
        query = "SELECT address FROM Address address WHERE address.city = :city"),
    @NamedQuery(
        name = Address.FIND_ALL_BY_STREET,
        query = "SELECT address FROM Address address WHERE address.street = :street"),
    @NamedQuery(
        name = Address.FIND_ALL_BY_POSTAL_CODE,
        query = "SELECT address FROM Address address WHERE address.postalCode = :postalCode"),
    @NamedQuery(
        name = Address.FIND_ALL_BY_STREET_NUMBER,
        query = "SELECT address FROM Address address WHERE address.streetNumber = :streetNumber")})
public class Address extends AbstractEntity {
  public static final String FIND_ALL_BY_COUNTRY = "Address.findAllByCountry";
  public static final String FIND_ALL_BY_CITY = "Address.findAllByCity";
  public static final String FIND_ALL_BY_STREET = "Address.findAllByStreet";
  public static final String FIND_ALL_BY_POSTAL_CODE = "Address.findAllByPostalCode";
  public static final String FIND_ALL_BY_STREET_NUMBER = "Address.findAllByStreetNumber";

  @Column(nullable = false)
  private String country;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String street;

  @Column(name = "postal_code", nullable = false)
  private String postalCode;

  @Column(name = "street_number", nullable = false)
  private Integer streetNumber;
}
