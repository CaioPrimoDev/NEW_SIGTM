package br.com.ifba.solicitacao.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CNPJ;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoCadastroDTO {

    @NotBlank(message = "O CNPJ é obrigatório")
    @CNPJ(message = "CNPJ inválido") // Valida o dígito verificador e formato
    private String cnpj;

    @NotBlank(message = "O nome da empresa é obrigatório")
    private String nomeEmpresa;

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;

    @NotNull
    private String horarioFuncionamento;
}
