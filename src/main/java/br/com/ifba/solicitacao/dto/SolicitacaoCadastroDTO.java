package br.com.ifba.solicitacao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoCadastroDTO {
    private String cnpj;
    private String nomeEmpresa;

    // O ID do usuário que quer virar parceiro
    private Long usuarioId;
}
