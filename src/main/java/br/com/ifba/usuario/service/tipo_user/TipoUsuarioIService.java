package br.com.ifba.usuario.service.tipo_user;

import br.com.ifba.usuario.entity.TipoUsuario;
import java.util.List;

/**
 *
 * @author CaioP
 */
public interface TipoUsuarioIService {
    boolean save(TipoUsuario tipoUsuario);
    void delete(Long id);
    List<TipoUsuario> findAll();
    TipoUsuario findById(Long id);
    void validarTipoUsuario(TipoUsuario tipoUsuario);
    TipoUsuario findByNome(String nome);
}
