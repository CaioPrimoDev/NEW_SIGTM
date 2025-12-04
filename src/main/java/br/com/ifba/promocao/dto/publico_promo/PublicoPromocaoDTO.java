package br.com.ifba.promocao.dto.publico_promo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO de Entrada (Cadastro e Atualização)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicoPromocaoDTO {
    // Validações poderiam ser adicionadas aqui (ex: @NotBlank)
    private String nome;
    private String descricao;
    private String faixaEtaria;
    private String interesse;
}
