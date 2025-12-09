package br.com.ifba.usuario.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCadastroDTO {

    @NotBlank(message = "O e-mail é obrigatório")
    @Email
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6)
    private String senha;

    // ID do tipo continua, pois o TIPO já existe no banco (ex: Admin, Comum)
    @NotNull
    private Long tipoUsuarioId;

    private String telefone;

    // --- DADOS DA PESSOA (Ao invés do ID) ---

    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido") // Se tiver a lib Hibernate Validator
    private String cpf;

    @NotBlank(message = "O nome é obrigatório") // Imagino que Pessoa tenha nome
    private String nome;
}
