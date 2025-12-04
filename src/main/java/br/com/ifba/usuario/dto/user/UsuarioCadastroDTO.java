package br.com.ifba.usuario.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCadastroDTO {
    private String email;
    private String senha;

    // IDs para vínculo (Relacionamentos)
    private Long tipoUsuarioId; // ID do Tipo (ex: ADMIN, COMUM)
    private Long pessoaId;      // ID da Pessoa já cadastrada (Gestor, Parceiro ou Comum)
}
