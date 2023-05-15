package pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = {})
@Pattern(regexp = "^[0-9]{10}$",
    message = "NIP must contain 10 digits")
public @interface Nip {
  String message() default "NIP must contain 10 digits";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
