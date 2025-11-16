package br.com.ifba.usuario.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.pessoa.entity.Pessoa;
import br.com.ifba.solicitacao.entity.Solicitacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@Getter @Setter
@Table(name = "usuario")
public class Usuario extends PersistenceEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private boolean ativo;

    @OneToOne
    private TipoUsuario tipo;

    @Column(nullable = false)
    private LocalDate ultimoLogin;

    // Associa o usuario a uma Pessoa (Gestor ou Parceiro)
    @OneToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    // Opcional: mappedBy
    @OneToMany(mappedBy = "usuario")
    private List<Solicitacao> solicitacoes;

    public Usuario() {
        this.ativo = true;
    }
}
