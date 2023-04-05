package pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = {})
@NotNull(message = "Field cannot be empty")
@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$",
         message = "Login must start with a letter and contain only letters and digits.")
@Size(min = 6, max = 20, message = "The length of the field must be between {min} and {max} characters")
public @interface Login {
    String message() default "\"Password must contain at least one uppercase letter \" +\n" +
            "                 \"and one special character from the following set: !@#$%^&+=\"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
