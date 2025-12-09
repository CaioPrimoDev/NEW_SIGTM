package br.com.ifba.reserva.dto;

import br.com.ifba.infrastructure.validation.OuPontoOuEvento;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@OuPontoOuEvento // Garante que pelo menos PontoTuristico ou evento seja null
public class ReservaCadastroDTO {

    /**
    * Necessária validações customizadas aqui
    **/
    // Data desejada para a visita
    // Formato esperado: "2023-12-25"
    @NotNull(message = "A data da reserva é obrigatória")
    @FutureOrPresent(message = "A data da reserva deve ser hoje ou no futuro")
    private LocalDate dataReserva;

    /**
     * Necessária validações customizadas aqui
     **/
    // Campos Opcionais na validação simples (lógica XOR deve ser feita no Service ou Custom Validator)
    // (Preencher UM, deixar o outro NULL)
    @Positive
    private Long pontoTuristicoId;

    @Positive
    private Long eventoId;
}



