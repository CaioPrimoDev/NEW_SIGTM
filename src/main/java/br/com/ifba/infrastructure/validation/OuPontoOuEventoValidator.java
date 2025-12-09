package br.com.ifba.infrastructure.validation;

import br.com.ifba.reserva.dto.ReservaCadastroDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OuPontoOuEventoValidator implements ConstraintValidator<OuPontoOuEvento, ReservaCadastroDTO> {

    @Override
    public boolean isValid(ReservaCadastroDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true; // Deixa @NotNull validar se o objeto é nulo
        }

        boolean temPonto = dto.getPontoTuristicoId() != null;
        boolean temEvento = dto.getEventoId() != null;

        // Regra XOR: (Tem um E não tem o outro) OU (Não tem um E tem o outro)
        // Simplificando: Os estados booleanos devem ser diferentes.
        // Se ambos forem true (tem os dois) -> Erro
        // Se ambos forem false (não tem nenhum) -> Erro
        return temPonto ^ temEvento; // O operador ^ é o XOR (Ou Exclusivo) em Java
    }
}
