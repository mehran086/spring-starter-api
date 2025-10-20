package com.codewithmosh.store.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(value= ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LowerCaseClass.class)
public @interface Lowercase {

    String message() default "All the letters should be lowercase";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
