package br.com.ifba.pessoa.other_users.parceiro.service;

import br.com.ifba.pessoa.other_users.parceiro.entity.Parceiro;
import br.com.ifba.usuario.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface ParceiroIService {

    boolean save(Parceiro user);
    void delete(Long id);
    List<Parceiro> findAll();
    Parceiro findById(Long id);
    List<Parceiro> findByNomeContainingIgnoreCase(String nome);
    Optional<Parceiro> findByCnpj(String cnpj);
    void validarParceiro(Parceiro user);
    Parceiro tornarParceiro(Usuario usuario);
    Usuario removerParceiria(Parceiro parceiro);

}
