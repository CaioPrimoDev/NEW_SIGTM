package br.com.ifba.promocao.dto.promo;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// DTO de Entrada (Cadastro e Atualização)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromocaoDTO {

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "As regras são obrigatórias")
    private String regras;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    // Datas de vigência
    @NotNull(message = "A data de início é obrigatória")
    @FutureOrPresent(message = "A data de início não pode ser no passado")
    private Date dataInicio;

    @NotNull(message = "A data de término é obrigatória")
    @Future(message = "A data de término deve ser no futuro")
    private Date dataTermino;

    @NotNull(message = "O tipo de promoção é obrigatório")
    private Long tipoPromocaoId;
}
