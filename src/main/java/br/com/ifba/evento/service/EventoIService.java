package br.com.ifba.evento.service;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.evento.entity.Evento;
import br.com.ifba.pessoa.other_users.parceiro.entity.Parceiro;

import java.util.List;

/**
 *
 * @author Casa
 */
public interface EventoIService {

    boolean save(Evento evento);

    void delete(Long id);

    List<Evento> findAll();

    Evento findById(Long id);

    List<Evento> findByCategoriaContainingIgnoreCase(String categoria);

    List<Evento> findByNomeContainingIgnoreCase(String eventoNome);

    void validarEvento(Evento evento);

    Evento adicionarEvento(Evento evento, Parceiro parceiro, Endereco endereco);
}
