package br.com.ifba.promocao.dto.promo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromocaoResponseDTO {
    private Long id;
    private String titulo;
    private String regras;
    private String descricao;
    private Date dataInicio;
    private Date dataTermino;

    // Dados Aninhados
    private String nomeUsuarioCriador;
    private String tituloTipoPromocao; // Apenas o nome do tipo para exibir na lista
}
