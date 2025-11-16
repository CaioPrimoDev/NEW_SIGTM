package br.com.ifba.pontoturistico.repository;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.pontoturistico.entity.PontoTuristico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PontoTuristicoRepository extends JpaRepository<PontoTuristico, Long> {

    List<PontoTuristico> findByNomeStartingWithIgnoreCase(String nome);

    // Método para o EnderecoService verificar se este endereço está em uso.
    boolean existsByEnderecoId(Long enderecoId);

    // Método para a validação de unicidade de endereço do PontoTuristicoService.
    Optional<PontoTuristico> findByEndereco(Endereco endereco);
}
