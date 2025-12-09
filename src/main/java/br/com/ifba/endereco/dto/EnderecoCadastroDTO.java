package br.com.ifba.endereco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoCadastroDTO {

    @NotBlank(message = "O estado é obrigatório")
    @Size(min = 2, max = 2, message = "Use a sigla do estado (ex: SP, RJ)")
    private String estado;

    @NotBlank(message = "A cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "O bairro é obrigatório")
    private String bairro;

    @NotBlank(message = "A rua é obrigatória")
    private String rua;

    @NotBlank(message = "O número é obrigatório")
    private String numero; // String pois pode ser "100A", "S/N"
}
