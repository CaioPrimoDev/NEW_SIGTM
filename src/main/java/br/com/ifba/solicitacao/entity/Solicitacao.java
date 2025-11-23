package br.com.ifba.solicitacao.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao")
@Getter
@Setter
public class Solicitacao extends PersistenceEntity {

    @Column(nullable = false)
    private String cnpj;
    @Column(nullable = false)
    private String nomeEmpresa;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    @Column(nullable=false)
    private LocalDateTime dataSolicitacao;
    @Column(nullable = false)
    private boolean solicitouParceria = false; // por padrão nenhum usuário fez solicita;cão

    public Solicitacao() {
        this.dataSolicitacao = LocalDateTime.now();
    }
}
