package br.com.ifba.avaliacoes.entity;

import br.com.ifba.evento.entity.Evento;
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

    // Agora é opcional (pode ser null se a avaliação for de um evento)
    @ManyToOne(optional = true)
    @JoinColumn(name = "ponto_turistico_id")
    private PontoTuristico pontoTuristico;

    // Relacionamento com Evento
    @ManyToOne(optional = true)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Construtor para Ponto Turístico
    public Avaliacao(int estrelas, String descricao, PontoTuristico pontoTuristico, Usuario usuario) {
        this.estrelas = estrelas;
        this.descricao = descricao;
        this.pontoTuristico = pontoTuristico;
        this.usuario = usuario;
        if (usuario.getPessoa() != null) {
            this.nomeAutor = usuario.getPessoa().getNome();
        }
    }

    // Construtor para Evento
    public Avaliacao(int estrelas, String descricao, Evento evento, Usuario usuario) {
        this.estrelas = estrelas;
        this.descricao = descricao;
        this.evento = evento;
        this.usuario = usuario;
        if (usuario.getPessoa() != null) {
            this.nomeAutor = usuario.getPessoa().getNome();
        }
    }
}