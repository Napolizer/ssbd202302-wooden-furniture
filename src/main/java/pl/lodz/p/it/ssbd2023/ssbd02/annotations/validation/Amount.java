package pl.lodz.p.it.ssbd2023.ssbd02.annotations.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Min;
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
@Pattern(regexp = "^[0-9]\\d*$", message = "Amount must be positive number")
@Min(value = 0, message = "Amount must be at least {value}")
public @interface Amount {
  String message() default "Amount must be positive number";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
