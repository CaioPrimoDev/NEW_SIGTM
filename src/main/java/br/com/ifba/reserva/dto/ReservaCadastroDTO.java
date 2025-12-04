package br.com.ifba.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaCadastroDTO {
    // Data desejada para a visita
    // Formato esperado: "2023-12-25"
    private LocalDate dataReserva;

    // Campos Opcionais (Preencher UM, deixar o outro NULL)
    private Long pontoTuristicoId;
    private Long eventoId;
}
