package br.com.ifba.sessao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessaoResponseDTO {
    private Long id;
    private String email;
    private String nome;        // Vem de usuario.getPessoa().getNome()
    private String tipoUsuario; // Vem de usuario.getTipo().getNome()

    // Campo opcional para confirmar que o login ocorreu
    private boolean logado = true;
}