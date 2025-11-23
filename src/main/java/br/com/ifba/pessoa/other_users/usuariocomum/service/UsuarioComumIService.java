package br.com.ifba.pessoa.other_users.usuariocomum.service;

import br.com.ifba.pessoa.other_users.usuariocomum.entity.UsuarioComum;

import java.util.List;

public interface UsuarioComumIService {

    boolean save(UsuarioComum usuario);

    void delete(Long id);

    List<UsuarioComum> findAll();

    UsuarioComum findById(Long id);

    List<UsuarioComum> findByNomeContainingIgnoreCase(String nome);
}
