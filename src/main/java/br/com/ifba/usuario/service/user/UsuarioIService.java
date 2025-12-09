package br.com.ifba.usuario.service.user;

import br.com.ifba.solicitacao.entity.Solicitacao;
import br.com.ifba.usuario.dto.user.UsuarioCadastroDTO;
import br.com.ifba.usuario.entity.Usuario;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioIService {

    Usuario save(UsuarioCadastroDTO dto);
    Usuario save(Usuario usuario);
    void delete(Long id);
    List<Usuario> findAll();
    Usuario findById(Long id);
    Usuario saveAndFlush(Usuario usuario);
    Usuario findByPessoaId(Long pessoaId);
    void validarUsuario(Usuario user);
    List<Solicitacao> findSolicitacoesByUsuarioId(@Param("id") Long id);
}
