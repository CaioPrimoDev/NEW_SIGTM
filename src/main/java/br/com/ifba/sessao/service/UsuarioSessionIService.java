package br.com.ifba.sessao.service;

import br.com.ifba.usuario.entity.Usuario;

public interface UsuarioSessionIService {
    Usuario validarLogin(String email, String senha);
    boolean isLogado();
    Usuario getUsuarioLogado();
    void registrarLoginNaSessao(Usuario usuario);
    void setEmailLogado(String email);
}
