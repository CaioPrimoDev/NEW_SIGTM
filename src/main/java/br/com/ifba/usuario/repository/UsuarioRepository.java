package br.com.ifba.usuario.repository;

import br.com.ifba.solicitacao.entity.Solicitacao;
import br.com.ifba.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    Usuario findByPessoaId(Long pessoaId);

    List<Usuario> findByPessoaNomeContainingIgnoreCase(String nome);

    // JOIN para navegar na lista e retornar os objetos Solicitacao (alias 's')
    @Query("SELECT s FROM Usuario u JOIN u.solicitacoes s WHERE u.id = :id")
    List<Solicitacao> findSolicitacoesByUsuarioId(@Param("id") Long id);
}

