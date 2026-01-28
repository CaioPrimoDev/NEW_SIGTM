package br.com.ifba.promocao.dto.tipo_promocao;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoPromocaoDTO {

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "A regra é obrigatória")
    private String regra;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

}
