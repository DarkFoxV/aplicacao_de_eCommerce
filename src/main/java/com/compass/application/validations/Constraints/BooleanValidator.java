package com.compass.application.validations.Constraints;

import com.compass.application.validations.ValidBoolean;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BooleanValidator implements ConstraintValidator<ValidBoolean, Boolean> {

    @Override
    public void initialize(ValidBoolean constraintAnnotation) {
    }

    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext context) {

        return value == Boolean.TRUE || value == Boolean.FALSE;
    }
}
