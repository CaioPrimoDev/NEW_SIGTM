package br.com.ifba.pessoa.other_users.gestor.service;

import br.com.ifba.pessoa.other_users.gestor.entity.Gestor;

import java.util.List;

/**
 *
 * @author CaioP
 */

public interface GestorIService {

    boolean save(Gestor user);
    void delete(Long id);
    List<Gestor> findAll();
    Gestor findById(Long id);
    List<Gestor> findByNomeContainingIgnoreCase(String nome);
    void validarGestor(Gestor user);

}