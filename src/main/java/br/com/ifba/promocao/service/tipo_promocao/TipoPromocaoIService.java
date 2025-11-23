package br.com.ifba.promocao.service.tipo_promocao;

import br.com.ifba.promocao.entity.TipoPromocao;

import java.util.List;

/**
 *
 * @author Joice
 */
public interface TipoPromocaoIService {

    TipoPromocao save(TipoPromocao tipoPromocao);
    TipoPromocao update(TipoPromocao tipoPromocao);
    void delete(Long id);
    List<TipoPromocao> findAll();
    TipoPromocao findById(Long id);
}
