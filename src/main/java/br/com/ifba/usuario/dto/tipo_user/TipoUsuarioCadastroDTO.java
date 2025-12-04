package br.com.ifba.usuario.dto.tipo_user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class TipoUsuarioCadastroDTO {
    private String nome;      // Ex: "ADMIN", "PARCEIRO"
    private String descricao; // Ex: "Acesso total ao sistema"
}
