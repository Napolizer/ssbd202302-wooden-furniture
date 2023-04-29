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
@Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$",
    message = "Password must contain at least one uppercase letter "
        + "and one special character from the following set: !@#$%^&+=")
@Size(min = 8, max = 32, message = "The length of the field must be between {min} and {max} characters.")
public @interface Password {
  String message() default "Password must contain at least one uppercase letter "
      + "and one special character from the following set: !@#$%^&+=";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
