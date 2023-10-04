package com.reece.addressbook.validation;

import com.reece.addressbook.common.RequestHttpHeadersEnum;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {RequestHttpHeaderValidator.class})
public @interface ValidRequestHeader {
    String message() default "Not a valid RequestHeader value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    RequestHttpHeadersEnum mandatoryHeaders();
}
