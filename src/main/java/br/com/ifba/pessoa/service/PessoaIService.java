package br.com.ifba.pessoa.service;

import br.com.ifba.pessoa.entity.Pessoa;

import java.util.List;

public interface PessoaIService {
    boolean save(Pessoa pessoa);
    void delete(Long id);
    List<Pessoa> findAll();
    Pessoa findById(Long id);
    List<Pessoa> findByNomeContainingIgnoreCase(String nome);
}
