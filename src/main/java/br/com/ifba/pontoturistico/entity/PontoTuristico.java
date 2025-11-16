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
    @JoinColumn(name = "gestor_id", nullable = false) // Cria a coluna 'gestor_id'
    private Gestor gestor; // A referência para o gestor que o cadastrou


    // Construtor manual garante a inicialização completa e correta do objeto
    public PontoTuristico(String nome, String descricao, Endereco endereco,
                          int nivelAcessibilidade, String horarioAbertura, String horarioFechamento, Gestor gestor) {
        super(nome, descricao, endereco, nivelAcessibilidade);

        // responsaveis por dizer qual o horario de funcionamento
        this.horarioFechamento = horarioFechamento;
        this.horarioAbertura = horarioAbertura;

        this.gestor = gestor;
    }

    @Override
    public String toString() {
        // Retorna o nome para a ComboBox.
        // Se o objeto não tiver nome, retorna uma string vazia para evitar erros.
        if (this.getNome() == null) {
            return "";
        }
        return this.getNome();
    }
}
