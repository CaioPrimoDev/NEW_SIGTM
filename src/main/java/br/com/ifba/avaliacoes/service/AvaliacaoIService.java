package br.com.ifba.avaliacoes.service;

import br.com.ifba.avaliacoes.entity.Avaliacao;

import java.util.List;

public interface AvaliacaoIService {
    List<Avaliacao> findAllByPonto(Long pontoId);
    List<Avaliacao> getMelhoresByPonto(Long pontoId);
    List<Avaliacao> getPioresByPonto(Long pontoId);
    List<Avaliacao> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndPontoTuristicoId(Long usuarioId, Long pontoId);

    Avaliacao findById(Long id);
    Avaliacao saveForPonto(Long pontoId, Avaliacao avaliacao);
    Avaliacao update(Long id, Avaliacao avaliacao);
    void delete(Long id);
}
