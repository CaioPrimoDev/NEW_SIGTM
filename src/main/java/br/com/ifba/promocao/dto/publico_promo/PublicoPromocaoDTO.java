package br.com.ifba.promocao.dto.publico_promo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicoPromocaoDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Size(max = 500)
    private String descricao;

    @NotBlank(message = "A faixa etária é obrigatória")
    private String faixaEtaria;

    @NotBlank(message = "O interesse é obrigatório")
    private String interesse;
}
