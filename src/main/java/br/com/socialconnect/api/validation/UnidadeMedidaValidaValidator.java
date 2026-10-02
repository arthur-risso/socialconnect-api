package br.com.socialconnect.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;
import java.util.Set;

// Aceita apenas as unidades usadas pela ONG no controle de estoque (sem diferenciar maiúsculas)
public class UnidadeMedidaValidaValidator implements ConstraintValidator<UnidadeMedidaValida, String> {

    private static final Set<String> UNIDADES_PERMITIDAS =
            Set.of("unidade", "kg", "g", "l", "ml", "pacote", "caixa", "fardo", "par");

    @Override
    public boolean isValid(String unidade, ConstraintValidatorContext context) {
        if (unidade == null || unidade.isBlank()) return true; // @NotBlank cuida da obrigatoriedade
        return UNIDADES_PERMITIDAS.contains(unidade.trim().toLowerCase(Locale.ROOT));
    }
}
