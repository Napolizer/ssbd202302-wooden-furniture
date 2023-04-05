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
@Pattern(regexp = "^\\d{2}-\\d{3}$", message = "Postal code must be in the format xx-xxx")
public @interface PostalCode {
    String message() default "Postal code must be in the format xx-xxx";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
