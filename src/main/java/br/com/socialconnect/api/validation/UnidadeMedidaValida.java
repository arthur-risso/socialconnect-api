package br.com.socialconnect.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UnidadeMedidaValidaValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface UnidadeMedidaValida {
    String message() default "{UnidadeMedidaValida.unidadeMedida}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
