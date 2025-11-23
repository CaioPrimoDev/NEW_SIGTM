package br.com.ifba.solicitacao.repository;

import br.com.ifba.solicitacao.entity.Solicitacao;
import br.com.ifba.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    // Todas as solicitações de um usuário
    List<Solicitacao> findByUsuario(Usuario usuario);

    // A primeira solicitação encontrada de um usuário (retorna Optional)
    Optional<Solicitacao> findFirstByUsuario(Usuario usuario);

    // Todas com parceria true
    List<Solicitacao> findBySolicitouParceriaTrue();

    // Busca por nome do usuário, ignorando maiúsculas/minúsculas
    List<Solicitacao> findByUsuarioPessoaNomeContainingIgnoreCase(String nome);

    // Busca por nome do usuário, com parceria true e usuário ativo
    List<Solicitacao> findByUsuarioPessoaNomeContainingIgnoreCaseAndSolicitouParceriaTrueAndUsuarioAtivoTrue(String nome);

    // Lista de usuários que fizeram solicitações (sem duplicatas)
    @Query("SELECT DISTINCT s.usuario FROM Solicitacao s")
    List<Usuario> findUsuariosQueFizeramSolicitacoes();
}


