package br.com.ifba.promocao.service.promo;

import br.com.ifba.promocao.entity.Promocao;

import java.util.List;

/**
 *
 * @author Joice
 */
public interface PromocaoIService {
    Promocao save(Promocao promocao);
    Promocao update(Promocao promocao);
    void delete(Promocao promocao);
    List<Promocao> findAll();
    List<Promocao> filtrarPromocoes(String termo, String tipo);
    Promocao findById(Long id);
}
