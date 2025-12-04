package br.com.ifba.pontoturistico.dto;

import br.com.ifba.endereco.dto.EnderecoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PontoTuristicoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private int nivelAcessibilidade;
    private String horarioAbertura;
    private String horarioFechamento;

    // Dados Aninhados
    private EnderecoResponseDTO endereco;
    private String nomeGestorResponsavel;
}
