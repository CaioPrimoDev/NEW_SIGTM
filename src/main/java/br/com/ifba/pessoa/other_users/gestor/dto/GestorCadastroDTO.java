package br.com.ifba.pessoa.other_users.gestor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GestorCadastroDTO {
    private String nome;
    private String telefone;

    private String matricula;
    private String cargo;
}
