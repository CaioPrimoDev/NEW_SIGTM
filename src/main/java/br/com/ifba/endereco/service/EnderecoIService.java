package br.com.ifba.endereco.service;

import br.com.ifba.endereco.entity.Endereco;

import java.util.List;

/**
 *
 * @author juant
 */
public interface EnderecoIService {

    Endereco save(Endereco endereco);
    Endereco update(Endereco endereco);
    void deleteById(Long id);
    List<Endereco> findAll();
    Endereco findById(Long id);
    Endereco encontrarOuCriarEndereco(Endereco dadosEndereco);
}
