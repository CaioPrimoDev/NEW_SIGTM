package br.com.ifba.usuario.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCadastroDTO {

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve ser válido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    // IDs para vínculo (Relacionamentos)
    @NotNull(message = "O ID do tipo de usuário é obrigatório")
    @Positive(message = "O ID deve ser um número positivo")
    private Long tipoUsuarioId;

    @NotNull(message = "O ID da pessoa é obrigatório")
    @Positive
    private Long pessoaId;
}
