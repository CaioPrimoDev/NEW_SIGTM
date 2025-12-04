package br.com.ifba.pessoa.other_users.parceiro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParceiroCadastroDTO {
    private String nome; // Nome do responsável/contato
    private String telefone;

    private String cnpj;
    private String nomeEmpresa;
    private String horarioFuncionamento;
}
