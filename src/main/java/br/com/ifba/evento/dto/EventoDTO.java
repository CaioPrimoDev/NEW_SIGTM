package br.com.ifba.evento.dto;

import br.com.ifba.endereco.dto.EnderecoCadastroDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO de Entrada
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoDTO {
    // Campos de ItemTuristico
    private String nome;
    private String descricao;
    private int nivelAcessibilidade;

    // Campos de Evento
    private LocalDateTime hora; // Atenção ao formato no JSON (ex: "2023-12-25T20:00:00")
    private LocalDate data;     // Atenção ao formato no JSON (ex: "2023-12-25")
    private String publicoAlvo;
    private String programacao;
    private String categoria;

    // Endereço Aninhado
    private EnderecoCadastroDTO endereco;
}
