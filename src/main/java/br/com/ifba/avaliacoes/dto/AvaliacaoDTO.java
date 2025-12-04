package br.com.ifba.avaliacoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoDTO {
    // Validações (Bean Validation) seriam ideais aqui
    private int estrelas;
    private String descricao;
}
