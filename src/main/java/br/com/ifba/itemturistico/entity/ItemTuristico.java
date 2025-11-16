package br.com.ifba.itemturistico.entity;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.infrastructure.entity.PersistenceEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@MappedSuperclass
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ItemTuristico extends PersistenceEntity implements Serializable {

    @Column(name = "nome", nullable = false)
    protected String nome;

    // Usando 'TEXT' para descrições longas.
    @Column(name = "descricao", columnDefinition = "TEXT")
    protected String descricao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "endereco_id", nullable = false) // O endereço é obrigatório
    protected Endereco endereco;

    @Column(name = "nivel_acessibilidade", nullable = false)
    protected int nivelAcessibilidade;
}