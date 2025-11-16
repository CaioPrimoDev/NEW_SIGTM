package br.com.ifba.solicitacao.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao")
public class Solicitacao extends PersistenceEntity {

    @ManyToOne(optional = false)
    private Usuario usuario;

    @Column(nullable=false)
    private LocalDateTime dataSolicitacao;

    @Column(nullable=false)
    private String status; // PENDENTE, APROVADO, RECUSADO
}
