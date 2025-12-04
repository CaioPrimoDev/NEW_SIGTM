package br.com.ifba.usuario.dto.tipo_user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoUsuarioResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
}