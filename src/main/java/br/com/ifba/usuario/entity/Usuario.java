package br.com.ifba.usuario.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.pessoa.entity.Pessoa;
import br.com.ifba.solicitacao.entity.Solicitacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @ManyToOne
    private TipoUsuario tipo;

    @Column(nullable = false)
    private LocalDate ultimoLogin;

    // Associa o usuario a uma Pessoa (Gestor ou Parceiro)
    @OneToOne
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    // Opcional: mappedBy
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Solicitacao> solicitacoes = new ArrayList<>();

    public Usuario() {
        this.ativo = true;
    }
}
