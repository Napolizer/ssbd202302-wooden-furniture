package pl.lodz.p.it.ssbd2023.ssbd02.mok.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation.*;

@Data
public class AccountRegisterDto {
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

    @NotNull
    @Positive(message = "Street number must be a positive integer")
    private Integer streetNumber;

    @PostalCode
    private String postalCode;

    @Login
    private String login;

    @Password
    private String password;

    @NotNull
    @Email(message = "Invalid email address format")
    private String email;

    @Locale
    private String locale;
}
