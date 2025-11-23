package br.com.ifba.pontoturistico.service;

import br.com.ifba.pontoturistico.entity.PontoTuristico;
import br.com.ifba.sessao.entity.UsuarioSession;

import java.util.List;

/**
 *
 * @author juant
 */
public interface PontoTuristicoIService {

    void verificaGestor(UsuarioSession userLogado);
    void save(PontoTuristico pontoTuristico);
    void update(PontoTuristico pontoTuristico);
    void delete(PontoTuristico pontoTuristico);
    List<PontoTuristico> findAll();
    PontoTuristico findById(Long id);
    List<PontoTuristico> findByNomeStartingWithIgnoreCase(String nome);
}
