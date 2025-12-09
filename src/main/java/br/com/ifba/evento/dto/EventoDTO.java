package br.com.ifba.evento.dto;

import br.com.ifba.endereco.dto.EnderecoCadastroDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoDTO {

    @NotBlank(message = "O nome do evento é obrigatório")
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @Min(1) @Max(5)
    private int nivelAcessibilidade;

    // Campos de Evento
    @NotNull(message = "A hora do evento é obrigatória")
    @FutureOrPresent(message = "A data/hora do evento deve ser no futuro ou presente")
    private LocalDateTime hora;

    @NotNull(message = "A data do evento é obrigatória")
    @FutureOrPresent(message = "A data do evento deve ser no futuro ou presente")
    private LocalDate data;

    @NotBlank(message = "O público alvo é obrigatório")
    private String publicoAlvo;

    @NotBlank(message = "A programação é obrigatória")
    private String programacao;

    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;

    // Endereço Aninhado
    @NotNull(message = "O endereço do evento é obrigatório")
    @Valid // <--- Validação em cascata
    private EnderecoCadastroDTO endereco;
}
