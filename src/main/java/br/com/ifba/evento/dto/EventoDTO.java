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

    @Min(value = 1, message = "O nível de acessibilidade deve ser no mínimo 1")
    @Max(value = 5, message = "O nível de acessibilidade deve ser no máximo 5")
    private int nivelAcessibilidade;

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

    // A validação @Valid aqui obriga o Spring a validar também os campos dentro de EnderecoCadastroDTO
    @NotNull(message = "O endereço do evento é obrigatório")
    @Valid
    private EnderecoCadastroDTO endereco;

    // O parceiro pode ser opcional dependendo da lógica (geralmente pego da sessão ou enviado por Admin)
    private Long parceiroId;
}