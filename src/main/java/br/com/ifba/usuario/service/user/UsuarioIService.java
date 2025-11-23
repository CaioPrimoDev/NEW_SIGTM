package br.com.ifba.usuario.service.user;

import br.com.ifba.solicitacao.entity.Solicitacao;
import br.com.ifba.usuario.entity.Usuario;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioIService {

    boolean save(Usuario user);
    void delete(Long id);
    List<Usuario> findAll();
    Usuario findById(Long id);
    Usuario findByPessoaId(Long pessoaId);
    void validarUsuario(Usuario user);
    List<Solicitacao> findSolicitacoesByUsuarioId(@Param("id") Long id);


}
