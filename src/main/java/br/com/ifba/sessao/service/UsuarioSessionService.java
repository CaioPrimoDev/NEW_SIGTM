package br.com.ifba.sessao.service;


import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioRepository;
import br.com.ifba.util.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioSessionService implements UsuarioSessionIService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Valida "login" pelo email e senha.
     * Lança RegraNegocioException se inválido.
     */
    @Override
    public Usuario validarLogin(String email, String senha) {
        log.info("Tentando login do usuário com email: {}", email);

        if (email == null || email.trim().isEmpty() || senha == null || senha.isEmpty()) {
            log.warn("Email ou senha nulos/invalidos");
            throw new RegraNegocioException("Email e senha são obrigatórios.");
        }

        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        if (usuario.isEmpty()) {
            log.warn("Usuário não encontrado: {}", email);
            throw new RegraNegocioException("Usuário ou senha inválidos.");
        }

        if (!usuario.get().getSenha().equals(senha)) {
            log.warn("Senha incorreta para o usuário: {}", email);
            throw new RegraNegocioException("Usuário ou senha inválidos.");
        }

        if (!usuario.get().isAtivo()) {
            log.warn("Usuário inativo: {}", email);
            throw new RegraNegocioException("Usuário inativo.");
        }


        log.info("Login válido para usuário: {}", email);
        return usuario.orElse(null);
    }
}

