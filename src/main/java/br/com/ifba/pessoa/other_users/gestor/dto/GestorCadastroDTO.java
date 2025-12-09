package br.com.ifba.pessoa.other_users.gestor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GestorCadastroDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "A matrícula é obrigatória")
    private String matricula;

    @NotBlank(message = "O cargo é obrigatório")
    private String cargo;
}
