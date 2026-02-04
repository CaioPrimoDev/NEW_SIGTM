package br.com.ifba.sessao.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.sessao.dto.LoginDTO;
import br.com.ifba.sessao.dto.SessaoResponseDTO;
import br.com.ifba.sessao.entity.UsuarioSession;
import br.com.ifba.sessao.service.UsuarioSessionIService;
import br.com.ifba.sessao.service.UsuarioSessionService;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioSessionIService service;
    private final UsuarioSession usuarioSession;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SessaoResponseDTO> login(@RequestBody @Valid LoginDTO dto) {

        Usuario usuarioValidado = service.validarLogin(dto.getEmail(), dto.getSenha());

        // Garanta que o bean injetado no controller receba o usuário
        this.usuarioSession.setUsuarioLogado(usuarioValidado); 
        
        // Se o seu service fizer outras coisas (como auditoria), pode manter a chamada abaixo,
        // mas a linha de cima é a que resolve o 401 do AvaliacaoController.
        service.registrarLoginNaSessao(usuarioValidado);

        // ... restante do código (mapping e return)
        SessaoResponseDTO response = mapper.map(usuarioValidado, SessaoResponseDTO.class);
        // ...
        return ResponseEntity.ok(response);
    }


    @PostMapping(value = "/logout") // Logout geralmente não tem body de entrada
    public ResponseEntity<Void> logout() {
        usuarioSession.limparSessao();
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/check")
    public ResponseEntity<SessaoResponseDTO> verificarSessao() {
        if (!usuarioSession.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioSession.getUsuarioLogado();
        SessaoResponseDTO response = mapper.map(usuario, SessaoResponseDTO.class);

        if (usuario.getPessoa() != null) response.setNome(usuario.getPessoa().getNome());
        if (usuario.getTipo() != null) response.setTipoUsuario(usuario.getTipo().getNome());

        return ResponseEntity.ok(response);
    }
}