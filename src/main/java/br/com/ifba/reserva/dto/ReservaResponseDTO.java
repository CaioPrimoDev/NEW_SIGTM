package br.com.ifba.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {
    private Long id;
    private String token;
    private LocalDate dataReserva;

    // Dados do Item Reservado (Simplificados)
    private String tipoItem; // "EVENTO" ou "PONTO_TURISTICO"
    private Long itemId;     // ID do evento ou ponto
    private String nomeItem; // Nome do evento ou ponto

    // Dados do Usuário (útil para o Gestor ver de quem é)
    private String nomeUsuario;
}
