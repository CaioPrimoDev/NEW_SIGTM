package br.com.ifba.endereco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoResponseDTO {
    private Long id;
    private String estado;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;

    // Campo extra para exibir formatado no front (igual ao toString da entidade)
    public String getEnderecoFormatado() {
        return String.format("%s, %s - %s, %s/%s",
                (rua != null ? rua : "Rua não informada"),
                (numero != null ? numero : "S/N"),
                bairro, cidade, estado);
    }
}
