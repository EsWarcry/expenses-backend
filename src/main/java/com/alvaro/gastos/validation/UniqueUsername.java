package com.alvaro.gastos.validation;

import com.alvaro.gastos.validators.UniqueUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {

    String message() default "El nombre de usuario ya está en uso por otra persona.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
