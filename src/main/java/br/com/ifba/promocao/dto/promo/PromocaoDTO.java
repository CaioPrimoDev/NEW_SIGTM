package br.com.ifba.promocao.dto.promo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// DTO de Entrada (Cadastro e Atualização)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromocaoDTO {
    private String titulo;
    private String regras;
    private String descricao;

    // Datas de vigência
    private Date dataInicio;
    private Date dataTermino;

    // ID do Tipo de Promoção (ex: "Desconto", "Sorteio")
    private Long tipoPromocaoId;
}
