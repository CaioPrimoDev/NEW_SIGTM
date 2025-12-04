package br.com.ifba.solicitacao.dto;

import br.com.ifba.usuario.dto.user.UsuarioResumoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoResponseDTO {
    private Long id;
    private String cnpj;
    private String nomeEmpresa;
    private LocalDateTime dataSolicitacao;
    private boolean solicitouParceria;

    // DTO Aninhado: Quem fez o pedido?
    private UsuarioResumoDTO usuario;
}
