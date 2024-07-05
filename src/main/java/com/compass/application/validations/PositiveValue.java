package com.compass.application.validations;

import com.compass.application.validations.constraitValidation.PositiveValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositiveValueValidator.class)
public @interface PositiveValue {

    String message() default "The value must be positive";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
