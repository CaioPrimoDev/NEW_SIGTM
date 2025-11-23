package br.com.ifba.solicitacao.service;

import br.com.ifba.solicitacao.entity.Solicitacao;
import br.com.ifba.usuario.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoIService {

    Solicitacao save(Solicitacao solicitacao);

    void delete(Long id);

    List<Solicitacao> findAll();

    Solicitacao findById(Long id);

    Optional<Solicitacao> findByUsuario(Usuario usuario);

    List<Solicitacao> findBySolicitouParceriaTrue();

    List<Solicitacao> findByNomeUsuarioComSolicitacaoAtiva(String nome);

    void validarSolicitacao(Solicitacao solicitacao);
}

