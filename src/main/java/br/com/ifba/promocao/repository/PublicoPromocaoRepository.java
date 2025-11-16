package br.com.ifba.promocao.repository;

import br.com.ifba.promocao.entity.PublicoPromocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicoPromocaoRepository extends JpaRepository<PublicoPromocao, Long> {

    // Busca públicos pelo nome (exemplo de filtro customizado)
    List<PublicoPromocao> findByNomeContainingIgnoreCase(String nome);
}
