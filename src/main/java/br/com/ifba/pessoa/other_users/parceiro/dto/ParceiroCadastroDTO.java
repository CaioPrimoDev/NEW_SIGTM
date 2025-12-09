package br.com.ifba.pessoa.other_users.parceiro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CNPJ;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParceiroCadastroDTO {

    @NotBlank(message = "O nome do responsável é obrigatório")
    private String nome;

    @NotBlank(message = "O telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "O CNPJ é obrigatório")
    @CNPJ(message = "CNPJ inválido")
    private String cnpj;

    @NotBlank(message = "O nome da empresa é obrigatório")
    private String nomeEmpresa;

    @NotBlank(message = "O horário de funcionamento é obrigatório")
    private String horarioFuncionamento;
}
