package br.com.ifba.promocao.dto.tipo_promocao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoPromocaoResponseDTO {
    private Long id;
    private String titulo;
    private String regra;
    private String descricao;

    // Dados aninhados
    private String nomeUsuarioCadastro;      // Apenas o nome do criador
}
