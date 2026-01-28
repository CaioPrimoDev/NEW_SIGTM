package br.com.ifba.promocao.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_promocao")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class TipoPromocao extends PersistenceEntity {

    // Campo obrigatório para título
    @Column(name = "titulo", nullable = false)
    private String titulo;

    // Campo obrigatório com limite de 500 caracteres
    @Column(name = "regra", nullable = false, length = 500)
    private String regra;

    // Campo obrigatório com limite de 1000 caracteres
    @Column(name = "descricao", nullable = false, length = 1000)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "id_usuario_cadastro", nullable = false)
    private Usuario UsuarioCadastro;

}