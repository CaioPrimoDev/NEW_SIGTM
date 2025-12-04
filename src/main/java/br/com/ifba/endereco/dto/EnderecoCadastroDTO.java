package br.com.ifba.endereco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoCadastroDTO {
    private String estado;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
}
