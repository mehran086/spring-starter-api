package com.codewithmosh.store.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LowerCaseClass implements ConstraintValidator<Lowercase,String> {
    @Override
    public void initialize(Lowercase constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(value==null) return true;
        return value.equals(value.toLowerCase());
    }
}
