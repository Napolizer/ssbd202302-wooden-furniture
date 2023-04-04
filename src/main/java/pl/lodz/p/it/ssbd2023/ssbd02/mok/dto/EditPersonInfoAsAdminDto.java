package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class EditPersonInfoAsAdminDto {

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String country;
    @NotNull
    private String city;
    @NotNull
    private String street;
    @NotNull
    private String postalCode;
    @NotNull
    private Integer streetNumber;
    @NotNull
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
}