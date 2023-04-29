package pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = {})
@NotNull(message = "Field cannot be empty")
@Pattern(regexp = "^[a-z]{2}$", message = "Locale must contain only two lowercase letters")
public @interface Locale {
  String message() default "Locale must contain only two lowercase letters";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
