package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Capitalized;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.PostalCode;

@Data
public class EditPersonInfoDto {

    @Capitalized
    private String firstName;

    @Capitalized
    private String lastName;

    @Capitalized
    private String country;
    @Capitalized
    private String city;
    @Capitalized
    private String street;
    @PostalCode
    private String postalCode;
    @NotNull
    @Positive(message = "Street number must be a positive integer")
    private Integer streetNumber;

    public EditPersonInfoDto(String firstName, String lastName, String country, String city, String street, String postalCode, Integer streetNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.city = city;
        this.street = street;
        this.postalCode = postalCode;
        this.streetNumber = streetNumber;
    }

    public EditPersonInfoDto() {

    }
}
