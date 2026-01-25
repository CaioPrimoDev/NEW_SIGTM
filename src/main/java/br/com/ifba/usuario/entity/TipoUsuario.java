package br.com.ifba.usuario.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoUsuario extends PersistenceEntity {

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String descricao;
}
