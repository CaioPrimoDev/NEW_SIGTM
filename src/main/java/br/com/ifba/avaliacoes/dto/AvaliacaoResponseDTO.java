package br.com.ifba.avaliacoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoResponseDTO {
    private Long id;
    private String nomeAutor; // Importante: Exibir quem fez
    private int estrelas;
    private String descricao;

    // Dados do Ponto Turístico (se houver)
    private Long pontoTuristicoId;
    private String nomePontoTuristico;

    // [NOVO] Dados do Evento (se houver)
    private Long eventoId;
    private String nomeEvento;
}