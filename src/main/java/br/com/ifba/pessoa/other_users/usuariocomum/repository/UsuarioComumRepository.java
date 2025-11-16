package br.com.ifba.pessoa.other_users.usuariocomum.repository;

import br.com.ifba.pessoa.other_users.usuariocomum.entity.UsuarioComum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioComumRepository extends JpaRepository<UsuarioComum, Long> {

    List<UsuarioComum> findByNomeContainingIgnoreCase(String nome);
}

