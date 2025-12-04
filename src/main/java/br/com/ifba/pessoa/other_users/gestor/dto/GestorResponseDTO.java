package br.com.ifba.pessoa.other_users.gestor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GestorResponseDTO {
    private Long id;
    private String nome;
    private String telefone;
    private String matricula;
    private String cargo;
    private LocalDateTime dataCadastro;
}
