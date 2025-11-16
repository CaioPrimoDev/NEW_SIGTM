package br.com.ifba.pessoa.other_users.parceiro.repository;

import br.com.ifba.pessoa.other_users.parceiro.entity.Parceiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParceiroRepository extends JpaRepository<Parceiro, Long> {

    Optional<Parceiro> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);

    List<Parceiro> findByNomeContainingIgnoreCase(String nome);
}

