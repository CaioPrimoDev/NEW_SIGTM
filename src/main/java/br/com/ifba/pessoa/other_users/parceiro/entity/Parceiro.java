package br.com.ifba.pessoa.other_users.parceiro.entity;

import br.com.ifba.evento.entity.Evento;
import br.com.ifba.pessoa.entity.Pessoa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "parceiro")
public class Parceiro extends Pessoa {
    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(nullable = false)
    private String nomeEmpresa;

    // Em analise
    @Column(nullable = false)
    private String horarioFuncionamento;

    @OneToMany(mappedBy = "parceiro", fetch = FetchType.LAZY)
    private List<Evento> eventos;

}
