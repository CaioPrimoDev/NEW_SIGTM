package br.com.ifba.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) // TYPE = Valida a Classe inteira
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OuPontoOuEventoValidator.class)
public @interface OuPontoOuEvento {

    String message() default "Informe apenas um: Ponto Turístico OU Evento (não ambos, nem nenhum).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

