package br.com.ifba.pessoa.other_users.usuariocomum.entity;

import br.com.ifba.pessoa.entity.Pessoa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Builder
    @Entity
    @Table(name = "usuario_comum")
    @Getter @Setter
    @NoArgsConstructor
@AllArgsConstructor
public class UsuarioComum extends Pessoa {
        @Column(unique = true)
        private String cpf;
}
