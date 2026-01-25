package br.com.ifba.sessao.service; // Ajuste seu pacote


import br.com.ifba.sessao.service.UsuarioSessionIService;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import br.com.ifba.infrastructure.exception.BusinessException; // Ajuste seu pacote

import java.io.Serializable;
import java.util.Optional;

@Service
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
@RequiredArgsConstructor
@Getter @Setter
public class UsuarioSessionService implements UsuarioSessionIService, Serializable {

    private static final long serialVersionUID = 1L;

    private final UsuarioRepository usuarioRepository;

    private String emailLogado;

    /**
     * Verifica se existe um email guardado na sessão
     */
    @Override
    public boolean isLogado() {
        return this.emailLogado != null && !this.emailLogado.isEmpty();
    }

    /**
     * Recupera o objeto completo do banco de dados na hora que precisa.
     * Isso evita erros de serialização e lazy loading.
     */
    @Override
    public Usuario getUsuarioLogado() {
        if (!isLogado()) {
            return null;
        }
        // Busca fresquinho no banco
        return usuarioRepository.findByEmail(this.emailLogado)
                .orElse(null);
    }

    /**
     * Valida senha e retorna o usuário (lógica de negócio)
     */
    @Override
    public Usuario validarLogin(String email, String senha) {
        log.info("Tentando login do usuário com email: {}", email);

        if (email == null || email.trim().isEmpty() || senha == null || senha.isEmpty()) {
            throw new BusinessException("Email e senha são obrigatórios.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new BusinessException("Usuário ou senha inválidos.");
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.getSenha().equals(senha)) {
            throw new BusinessException("Usuário ou senha inválidos.");
        }

        if (!usuario.isAtivo()) {
            throw new BusinessException("Usuário inativo.");
        }

        return usuario;
    }

    // Método auxiliar para salvar na sessão explicitamente
    public void registrarLoginNaSessao(Usuario usuario) {
        this.emailLogado = usuario.getEmail();
        log.info("Sessão vinculada ao email: {}", this.emailLogado);
    }

    public void logout() {
        this.emailLogado = null;
    }
}