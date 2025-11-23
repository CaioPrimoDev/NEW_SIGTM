package br.com.ifba.reserva.service;

import br.com.ifba.reserva.entity.Reserva;
import br.com.ifba.usuario.entity.Usuario;

import java.util.List;

/**
 *
 * @author juant
 */
public interface ReservaIService {
    // metodos abstratos para ser implementado na classe
    List<Reserva> findByUsuario(Usuario usuario);
    void save(Reserva reserva);
    void delete(Reserva reserva);
    List<Reserva> findAll();
    Reserva findById(Long id);
    List<Reserva> findByTokenContainingIgnoreCase(String token);
}
