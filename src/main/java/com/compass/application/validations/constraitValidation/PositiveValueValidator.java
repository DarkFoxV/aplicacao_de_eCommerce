package com.compass.application.validations.constraitValidation;

import com.compass.application.validations.PositiveValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveValueValidator implements ConstraintValidator<PositiveValue, Double> {

    @Override
    public void initialize(PositiveValue constraintAnnotation) {
        // Método de inicialização, caso necessário
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        return value != null && value > 0;
    }

}
