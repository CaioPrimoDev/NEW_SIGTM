package br.com.ifba.promocao.repository;

import br.com.ifba.promocao.entity.TipoPromocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPromocaoRepository extends JpaRepository<TipoPromocao, Long> {
    TipoPromocao findByTitulo(String titulo);

    public boolean existsByTitulo(String nome);
}
