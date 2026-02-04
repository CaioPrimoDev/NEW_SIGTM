package br.com.ifba.avaliacoes.service;

import br.com.ifba.avaliacoes.entity.Avaliacao;
import java.util.List;

public interface AvaliacaoIService {

    // ================= PONTOS TURÍSTICOS =================
    List<Avaliacao> findAllByPonto(Long pontoId);
    List<Avaliacao> getMelhoresByPonto(Long pontoId);
    List<Avaliacao> getPioresByPonto(Long pontoId);
    boolean existsByUsuarioIdAndPontoTuristicoId(Long usuarioId, Long pontoId);

    // ================= EVENTOS [NOVO] =================
    // Este é o método que está faltando e causando o erro na linha 80
    List<Avaliacao> findAllByEvento(Long eventoId);

    // ================= GERAL =================
    List<Avaliacao> findByUsuarioId(Long usuarioId);
    Avaliacao findById(Long id);

    // ================= PERSISTÊNCIA =================
    Avaliacao saveForPonto(Long pontoId, Avaliacao avaliacao);

    // Este também deve estar aqui para evitar erro no outro método novo
    Avaliacao saveForEvento(Long eventoId, Avaliacao avaliacao);

    Avaliacao update(Long id, Avaliacao avaliacao);
    void delete(Long id);
}