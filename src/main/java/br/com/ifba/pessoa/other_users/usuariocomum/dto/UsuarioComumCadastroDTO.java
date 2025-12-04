package br.com.ifba.pessoa.other_users.usuariocomum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioComumCadastroDTO {
    private String nome;
    private String telefone;

    private String cpf;
}
