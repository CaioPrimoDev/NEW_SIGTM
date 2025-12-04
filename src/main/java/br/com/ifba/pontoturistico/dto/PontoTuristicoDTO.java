package br.com.ifba.pontoturistico.dto;

import br.com.ifba.endereco.dto.EnderecoCadastroDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PontoTuristicoDTO {
    // Dados do ItemTuristico
    private String nome;
    private String descricao;
    private int nivelAcessibilidade;

    // Dados específicos de PontoTuristico
    private String horarioAbertura;
    private String horarioFechamento;

    // Endereço Aninhado (O Service vai usar encontrarOuCriarEndereco)
    private EnderecoCadastroDTO endereco;
}
