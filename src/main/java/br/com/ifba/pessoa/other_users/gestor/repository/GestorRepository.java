package br.com.ifba.pessoa.other_users.gestor.repository;

import br.com.ifba.pessoa.other_users.gestor.entity.Gestor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GestorRepository extends JpaRepository<Gestor, Long> {

    List<Gestor> findByNomeContainingIgnoreCase(String nome);
}

