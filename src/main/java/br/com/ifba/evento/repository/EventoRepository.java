package br.com.ifba.evento.repository;

import br.com.ifba.evento.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByCategoriaContainingIgnoreCase(String categoria);//procurar eventos pela categoria

    List <Evento> findByNomeContainingIgnoreCase(String eventoNome);//procurar pelo  nome do evento
}
