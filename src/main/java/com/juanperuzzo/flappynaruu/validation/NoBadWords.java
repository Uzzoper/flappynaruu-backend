package com.juanperuzzo.flappynaruu.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BadWordsValidator.class)
public @interface NoBadWords {

    String message() default "Nickname cannot contain bad words.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}