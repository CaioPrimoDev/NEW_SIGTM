package br.com.ifba.solicitacao.repository;

import br.com.ifba.solicitacao.entity.Solicitacao;
import br.com.ifba.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findByUsuario(Usuario usuario);

    List<Solicitacao> findByStatus(String status);

    List<Solicitacao> findByUsuarioPessoaNomeContainingIgnoreCase(String nome);

    @Query("SELECT DISTINCT s.usuario FROM Solicitacao s")
    List<Usuario> findUsuariosQueFizeramSolicitacoes();
}

