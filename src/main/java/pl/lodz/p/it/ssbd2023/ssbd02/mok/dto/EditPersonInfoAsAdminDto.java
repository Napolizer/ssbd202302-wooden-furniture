package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.Capitalized;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.PostalCode;

@Data
public class EditPersonInfoAsAdminDto {

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
    @Email
    private String email;

    public EditPersonInfoAsAdminDto(String firstName, String lastName, String country, String city, String street, String postalCode, Integer streetNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.city = city;
        this.street = street;
        this.postalCode = postalCode;
        this.streetNumber = streetNumber;
        this.email = email;
    }

    public EditPersonInfoAsAdminDto() {

    }
}