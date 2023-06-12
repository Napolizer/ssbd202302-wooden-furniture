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
@Pattern(regexp = "^[$]2[abxy]?[$](?:0[4-9]|[12][0-9]|3[01])[$][./0-9a-zA-Z]{53}$")
public @interface Hash {
  String message() default "Hash is in invalid format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
