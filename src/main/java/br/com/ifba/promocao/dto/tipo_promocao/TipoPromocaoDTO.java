package br.com.ifba.promocao.dto.tipo_promocao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoPromocaoDTO {
    private String titulo;
    private String regra;
    private String descricao;

    private Long publicoAlvoId;
}
