package br.com.ifba.usuario.repository;

import br.com.ifba.usuario.entity.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {

    TipoUsuario findByNome(String nome);
    List<TipoUsuario> findByNomeContainingIgnoreCase(String nome);
    boolean existsByNome(String nome);
}

