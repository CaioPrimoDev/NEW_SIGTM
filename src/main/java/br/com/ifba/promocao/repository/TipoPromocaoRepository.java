package br.com.ifba.promocao.repository;

import br.com.ifba.promocao.entity.TipoPromocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoPromocaoRepository extends JpaRepository<TipoPromocao, Long> {
    List<TipoPromocao> findByTitulo(String titulo);
    public boolean existsByTitulo(String nome);
}
