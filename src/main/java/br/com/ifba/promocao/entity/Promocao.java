package br.com.ifba.promocao.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity //marca a classe como uma entidade JPA (será mapeada para tabela no banco)
@Table(name="promocoes") // define o nome da tabela no banco como "promocoes"
@NoArgsConstructor // cria um construtor sem argumentos
@AllArgsConstructor //cria um construtor com todos os argumentos
@Getter //cria os métos getters
@Setter //cria os métosdo setters
@ToString // gera o método toString() para a classe
public class Promocao extends PersistenceEntity {

    @Column(name = "titulo") // Mapeia o campo 'titulo' para a coluna 'titulo' na tabela
    private String titulo;

    @Column(name = "regras") // Mapeia o campo 'regras' para a coluna 'regras' na tabela
    private String regras;

    @Column(name = "descricao") // Mapeia o campo 'descricao' para coluna 'descricao'
    private String descricao;

    @Column(name = "dataInicio")// Mapeia o campo 'dataInicio' para coluna 'dataInicio'
    private Date dataInicio;

    @Column(name = "dataTermino") // Mapeia o campo 'dataTermino' para coluna 'dataTermino'
    private Date dataTermino;

    @ManyToOne
    @JoinColumn(name = "usuario_criador_id", nullable = false)
    private Usuario usuarioCriador;

    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false) // Mapeia o campo 'tipo' para coluna 'tipo'
    private TipoPromocao tipo; //variavel para guardar o tipo, se é promoção, cupom ou pacote
}
