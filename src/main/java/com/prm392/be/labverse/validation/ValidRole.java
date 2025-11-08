package com.prm392.be.labverse.validation;


import com.prm392.be.labverse.validation.validator.RoleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RoleValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRole {
    String message() default "Invalid role";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
