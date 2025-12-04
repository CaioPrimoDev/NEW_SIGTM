package br.com.ifba.avaliacoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoResponseDTO {
    private Long id;
    private String nomeAutor; // Importante: Exibir quem fez
    private int estrelas;
    private String descricao;

    // Opcional: Para saber a qual ponto se refere (útil em listas gerais)
    private Long pontoTuristicoId;
    private String nomePontoTuristico;
}
