package pl.lodz.p.it.ssbd2023.ssbd02.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Embeddable
@SuperBuilder
public class Address extends AbstractEntity{
    @Column(name="COUNTRY")
    private String country;
    @Column(name="CITY")
    private String city;
    @Column(name="STREET")
    private String street;
    @Column(name="POSTAL_CODE")
    private String postalCode;
    @Column(name="STREET_NUMBER")
    private Integer streetNumber;
}