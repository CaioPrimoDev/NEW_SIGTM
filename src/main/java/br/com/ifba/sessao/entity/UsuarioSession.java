package br.com.ifba.sessao.entity;

import br.com.ifba.usuario.entity.Usuario;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@Getter @Setter
public class UsuarioSession {

    private Usuario usuarioLogado;

    public void limparSessao() {
        this.usuarioLogado = null;
    }

    public boolean isLogado() {
        return usuarioLogado != null;
    }
}
