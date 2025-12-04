package br.com.ifba.pessoa.other_users.usuariocomum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioComumResponseDTO {
    private Long id;
    private String nome;
    private String telefone;
    private String cpf;
    private LocalDateTime dataCadastro; // Herdado de Pessoa
}
