package br.com.ifba.avaliacoes.repository;

import br.com.ifba.avaliacoes.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    // Todas as avaliações do ponto
    List<Avaliacao> findByPontoTuristicoIdOrderByEstrelasDesc(Long pontoId);

    // Melhores avaliações (estrelas >= param) do ponto, ordenadas desc
    List<Avaliacao> findByPontoTuristicoIdAndEstrelasGreaterThanEqualOrderByEstrelasDesc(Long pontoId, int estrelas);

    // Piores avaliações (estrelas <= param) do ponto, ordenadas asc
    List<Avaliacao> findByPontoTuristicoIdAndEstrelasLessThanEqualOrderByEstrelasAsc(Long pontoId, int estrelas);

    // Avaliações feitas por um usuário
    List<Avaliacao> findByUsuarioId(Long usuarioId);

    // Verifica se o usuário já avaliou o ponto turístico
    boolean existsByUsuarioIdAndPontoTuristicoId(Long usuarioId, Long pontoId);

    // Todas as avaliações do evento
    List<Avaliacao> findByEventoIdOrderByEstrelasDesc(Long eventoId);

    // Verifica se o usuário já avaliou o evento
    boolean existsByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);
}
