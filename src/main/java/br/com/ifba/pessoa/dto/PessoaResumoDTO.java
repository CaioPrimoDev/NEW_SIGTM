package br.com.ifba.pessoa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaResumoDTO {
    private Long id;
    private String nome;
    private String telefone;
}
