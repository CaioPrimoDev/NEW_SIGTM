package br.com.ifba.evento.entity;

import br.com.ifba.itemturistico.entity.ItemTuristico;
import br.com.ifba.pessoa.other_users.parceiro.entity.Parceiro;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Evento extends ItemTuristico {
    @Column(nullable = false)
    LocalDateTime hora;

    @Column(nullable = false)
    LocalDate data;

    @Column(nullable = false)
    String publicoAlvo;

    @Column(nullable = false)
    String programacao;

    @Column(nullable = false)
    String categoria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parceiro_id", nullable = false)
    private Parceiro parceiro;  // Atributo referenciado no mappedBy

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