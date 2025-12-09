package br.com.ifba.avaliacoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoDTO {

    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    private int estrelas;

    @Size(max = 500, message = "O comentário deve ter no máximo 500 caracteres")

    // Se a descrição for opcional, não use @NotBlank. Se for obrigatória, adicione @NotBlank.
    private String descricao;
}
