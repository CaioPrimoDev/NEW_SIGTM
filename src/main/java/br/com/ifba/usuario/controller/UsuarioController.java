package br.com.ifba.usuario.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.solicitacao.dto.SolicitacaoResponseDTO;
import br.com.ifba.solicitacao.entity.Solicitacao;
import br.com.ifba.usuario.dto.user.UsuarioCadastroDTO;
import br.com.ifba.usuario.dto.user.UsuarioResponseDTO;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.user.UsuarioIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioIService service;
    private final ObjectMapperUtill mapper;

    // --- SALVAR (Create) ---
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDTO> salvar(@RequestBody @Valid UsuarioCadastroDTO dto) {
        Usuario usuario = mapper.map(dto, Usuario.class);
        service.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(usuario, UsuarioResponseDTO.class));
    }

    // --- DELETAR ---
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- LISTAR TODOS ---
    @GetMapping(value = "/findall")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(mapListToDto(service.findAll()));
    }

    // --- BUSCAR POR ID ---
    @GetMapping(value = "/find/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        Usuario usuario = service.findById(id);
        return ResponseEntity.ok(mapper.map(usuario, UsuarioResponseDTO.class));
    }

    // --- BUSCAR POR ID DA PESSOA ---
    @GetMapping(value = "/find/pessoa/{pessoaId}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorPessoaId(@PathVariable Long pessoaId) {
        Usuario usuario = service.findByPessoaId(pessoaId);
        // Nota: Se o service retornar null, é ideal lançar ResourceNotFoundException lá dentro.
        // Se retornar null aqui, mantemos o check:
        if (usuario == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(mapper.map(usuario, UsuarioResponseDTO.class));
    }

    // --- BUSCAR SOLICITAÇÕES DO USUÁRIO ---
    @GetMapping(value = "/find/{id}/solicitacoes")
    public ResponseEntity<List<SolicitacaoResponseDTO>> listarSolicitacoes(@PathVariable Long id) {
        List<Solicitacao> solicitacoes = service.findSolicitacoesByUsuarioId(id);

        List<SolicitacaoResponseDTO> dtos = solicitacoes.stream()
                .map(s -> mapper.map(s, SolicitacaoResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    private List<UsuarioResponseDTO> mapListToDto(List<Usuario> lista) {
        return lista.stream()
                .map(u -> mapper.map(u, UsuarioResponseDTO.class))
                .collect(Collectors.toList());
    }
}
