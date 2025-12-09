package br.com.ifba.usuario.dto.tipo_user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoUsuarioCadastroDTO {

    @NotBlank(message = "O nome do tipo é obrigatório")
    @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
    private String nome;      // Ex: "ADMIN"

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 255)
    private String descricao;
}
