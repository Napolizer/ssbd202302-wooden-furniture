package pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = {})
@Pattern(regexp = "^[A-ZĄĆĘŁŃÓŚŹŻ][a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\s\\d.-]*$",
    message = "Field must start with a capital letter")
@Size(min = 1, max = 32, message = "The length of the field must be between {min} and {max} characters")
public @interface Company {
  String message() default "Field must start with a capital letter";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
