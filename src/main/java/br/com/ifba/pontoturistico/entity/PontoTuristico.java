package br.com.ifba.pontoturistico.entity;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.itemturistico.entity.ItemTuristico;
import br.com.ifba.pessoa.other_users.gestor.entity.Gestor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pontos_turisticos")
@Getter
@Setter
@NoArgsConstructor
public class PontoTuristico extends ItemTuristico {

    @Column(name = "horario_abertura")
    private String horarioAbertura;

    @Column(name = "horario_fechamento")
    private String horarioFechamento;

    @ManyToOne
    // ALTERAÇÃO 1: Mude nullable para true para permitir nulos no banco
    @JoinColumn(name = "gestor_id")
    private Gestor gestor;

    // ALTERAÇÃO 2: Remova 'Gestor gestor' dos parâmetros deste construtor
    public PontoTuristico(String nome, String descricao, Endereco endereco,
                          int nivelAcessibilidade, String horarioAbertura, String horarioFechamento) {
        super(nome, descricao, endereco, nivelAcessibilidade);

        this.horarioFechamento = horarioFechamento;
        this.horarioAbertura = horarioAbertura;
        this.gestor = gestor;
    }

    @Override
    public String toString() {
        if (this.getNome() == null) {
            return "";
        }
        return this.getNome();
    }
}