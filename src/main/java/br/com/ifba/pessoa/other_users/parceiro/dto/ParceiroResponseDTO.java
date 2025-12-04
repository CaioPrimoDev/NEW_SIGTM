package br.com.ifba.pessoa.other_users.parceiro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParceiroResponseDTO {
    private Long id;
    private String nome;
    private String telefone;
    private String cnpj;
    private String nomeEmpresa;
    private String horarioFuncionamento;
    private LocalDateTime dataCadastro;

    // Nota: Não incluí a lista de Eventos aqui para manter o resumo leve.
    // Eventos devem ser buscados em um endpoint específico ou DTO detalhado.
}
