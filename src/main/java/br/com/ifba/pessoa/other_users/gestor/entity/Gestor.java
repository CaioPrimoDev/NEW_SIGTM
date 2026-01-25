package br.com.ifba.pessoa.other_users.gestor.entity;

import br.com.ifba.pessoa.entity.Pessoa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "gestor")
public class Gestor extends Pessoa {

    @Column(nullable = false, unique = true)
    private String matricula;

    @Column(nullable = false)
    private String cargo;

}
