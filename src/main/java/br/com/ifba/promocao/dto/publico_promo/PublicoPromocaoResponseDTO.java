package br.com.ifba.promocao.dto.publico_promo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO de Saída (Response)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicoPromocaoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String faixaEtaria;
    private String interesse;

    // Mostra quem criou (apenas info básica)
    private String nomeUsuarioCadastro;
}
