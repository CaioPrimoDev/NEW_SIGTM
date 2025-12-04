package br.com.ifba.evento.dto;

import br.com.ifba.endereco.dto.EnderecoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private int nivelAcessibilidade;

    private LocalDateTime hora;
    private LocalDate data;
    private String publicoAlvo;
    private String programacao;
    private String categoria;

    // Dados Aninhados
    private EnderecoResponseDTO endereco;
    private String nomeParceiroResponsavel; // Apenas o nome da empresa/parceiro
}
