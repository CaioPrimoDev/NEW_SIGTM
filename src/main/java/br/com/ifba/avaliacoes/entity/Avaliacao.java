package br.com.ifba.avaliacoes.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.pontoturistico.entity.PontoTuristico;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "avaliacao")
@Getter
@Setter
@NoArgsConstructor
public class Avaliacao extends PersistenceEntity {

    @NotBlank
    @Column(nullable = false)
    private String nomeAutor;

    @Column(nullable = false)
    private int estrelas;

    @Column(nullable = false, length = 500)
    private String descricao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ponto_turistico_id")
    private PontoTuristico pontoTuristico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Avaliacao(int estrelas, String descricao, PontoTuristico pontoTuristico, Usuario usuario) {
        this.estrelas = estrelas;
        this.descricao = descricao;
        this.pontoTuristico = pontoTuristico;
        this.usuario = usuario;
        this.nomeAutor = usuario.getPessoa().getNome();
    }
}
