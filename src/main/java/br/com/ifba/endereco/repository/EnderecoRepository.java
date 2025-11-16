package br.com.ifba.endereco.repository;

import br.com.ifba.endereco.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    Optional<Endereco> findByEstadoAndCidadeAndBairroAndRuaAndNumero(
            String estado, String cidade, String bairro, String rua, String numero
    );
}

