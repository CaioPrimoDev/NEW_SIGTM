package br.com.ifba.sessao.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.sessao.dto.LoginDTO;
import br.com.ifba.sessao.dto.SessaoResponseDTO;
import br.com.ifba.sessao.entity.UsuarioSession;
import br.com.ifba.sessao.service.UsuarioSessionService;
import br.com.ifba.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioSessionService service; // Sua lógica de validação
    private final UsuarioSession usuarioSession; // Seu componente de Sessão
    private final ObjectMapperUtill mapper;

    /**
     * Endpoint de Login.
     * Recebe Email/Senha -> Valida -> Atualiza a Sessão -> Retorna dados do Usuário.
     */
    @PostMapping("/login")
    public ResponseEntity<SessaoResponseDTO> login(@RequestBody LoginDTO dto) {

        // 1. Valida as credenciais usando o Service que você criou
        // Se falhar, o Service lança RegraNegocioException (capturado pelo Handler global)
        Usuario usuarioValidado = service.validarLogin(dto.getEmail(), dto.getSenha());

        // 2. Armazena o usuário no componente de Sessão (@SessionScope)
        usuarioSession.setUsuarioLogado(usuarioValidado);

        // 3. Prepara o DTO de resposta
        SessaoResponseDTO response = mapper.map(usuarioValidado, SessaoResponseDTO.class);

        // Preenchimento manual de campos aninhados (caso o Mapper não esteja configurado para deep mapping)
        if (usuarioValidado.getPessoa() != null) {
            response.setNome(usuarioValidado.getPessoa().getNome());
        }
        if (usuarioValidado.getTipo() != null) {
            response.setTipoUsuario(usuarioValidado.getTipo().getNome());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de Logout.
     * Limpa os dados da sessão no servidor.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        usuarioSession.limparSessao();
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint auxiliar para verificar se a sessão ainda está viva.
     * Útil para o frontend saber se precisa redirecionar para login ao recarregar a página.
     */
    @GetMapping("/check")
    public ResponseEntity<SessaoResponseDTO> verificarSessao() {
        if (!usuarioSession.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioSession.getUsuarioLogado();

        // Retorna os dados do usuário que está na memória
        SessaoResponseDTO response = mapper.map(usuario, SessaoResponseDTO.class);

        // (Reaplicar mapeamento manual se necessário ou configurar ModelMapper)
        if (usuario.getPessoa() != null) response.setNome(usuario.getPessoa().getNome());
        if (usuario.getTipo() != null) response.setTipoUsuario(usuario.getTipo().getNome());

        return ResponseEntity.ok(response);
    }
}