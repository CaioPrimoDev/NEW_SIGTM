package br.com.ifba.avaliacoes.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.pontoturistico.entity.PontoTuristico;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao extends PersistenceEntity {

    @NotBlank
    @Column(nullable = false)
    private String nomeAutor;

    @Min(1) @Max(5)
    @Column(nullable = false)
    private int estrelas;

    @Column(nullable = true, length = 2000)
    private String descricao;

    @ManyToOne(optional = false, fetch = FetchType.LAZY) // Talvés eu tenha que mudar para EAGER
    @JoinColumn(name = "ponto_turistico_id", nullable = false)
    private PontoTuristico pontoTuristico;

}
