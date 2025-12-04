package br.com.ifba.usuario.dto.user;

import br.com.ifba.pessoa.dto.PessoaResumoDTO;
import br.com.ifba.usuario.dto.tipo_user.TipoUsuarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String email;
    private boolean ativo;
    private LocalDate ultimoLogin;

    // DTOs Aninhados (Resumos)
    private TipoUsuarioResponseDTO tipo; // Reutilizando o DTO criado anteriormente
    private PessoaResumoDTO pessoa;      // DTO simples apenas para mostrar quem é
}
