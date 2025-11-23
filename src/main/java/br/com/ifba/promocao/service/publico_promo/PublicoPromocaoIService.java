package br.com.ifba.promocao.service.publico_promo;

import br.com.ifba.promocao.entity.PublicoPromocao;

import java.util.List;

/**
 *
 * @author Joice
 */
public interface PublicoPromocaoIService {

    PublicoPromocao save(PublicoPromocao publicoPromocao);

    PublicoPromocao update(PublicoPromocao publicoPromocao);

    void delete(Long id);

    List<PublicoPromocao> findAll();

    PublicoPromocao findById(Long id);
}
