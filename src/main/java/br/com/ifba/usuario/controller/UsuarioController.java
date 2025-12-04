package br.com.ifba.usuario.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.solicitacao.dto.SolicitacaoResponseDTO;
import br.com.ifba.solicitacao.entity.Solicitacao;
import br.com.ifba.usuario.dto.user.UsuarioCadastroDTO;
import br.com.ifba.usuario.dto.user.UsuarioResponseDTO;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.user.UsuarioIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioIService service;
    private final ObjectMapperUtill mapper;

    // 1. SALVAR (Create/Update)
    @PostMapping("/save")
    public ResponseEntity<UsuarioResponseDTO> salvar(@RequestBody UsuarioCadastroDTO dto) {
        // Mapeia DTO -> Entidade
        // O Mapper deve ser capaz de traduzir pessoaId e tipoUsuarioId para as Entidades.
        Usuario usuario = mapper.map(dto, Usuario.class);

        // O Service valida (verifica se Pessoa tem nome) e salva
        service.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(usuario, UsuarioResponseDTO.class));
    }

    // 2. DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 3. LISTAR TODOS
    @GetMapping("/findall")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(mapListToDto(service.findAll()));
    }

    // 4. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        Usuario usuario = service.findById(id);
        return ResponseEntity.ok(mapper.map(usuario, UsuarioResponseDTO.class));
    }

    // 5. BUSCAR POR ID DA PESSOA
    // Endpoint: GET /usuarios/pessoa/{pessoaId}
    @GetMapping("/pessoa/{pessoaId}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorPessoaId(@PathVariable Long pessoaId) {
        Usuario usuario = service.findByPessoaId(pessoaId);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapper.map(usuario, UsuarioResponseDTO.class));
    }

    // 6. BUSCAR SOLICITAÇÕES DO USUÁRIO
    // Endpoint: GET /usuarios/{id}/solicitacoes
    @GetMapping("/{id}/solicitacoes")
    public ResponseEntity<List<SolicitacaoResponseDTO>> listarSolicitacoes(@PathVariable Long id) {
        // O Service lança exceção se não achar o usuário ou se a lista for nula/vazia
        List<Solicitacao> solicitacoes = service.findSolicitacoesByUsuarioId(id);

        // Mapeia a lista de solicitações para DTOs
        List<SolicitacaoResponseDTO> dtos = solicitacoes.stream()
                .map(s -> mapper.map(s, SolicitacaoResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // --- Auxiliar ---
    private List<UsuarioResponseDTO> mapListToDto(List<Usuario> lista) {
        return lista.stream()
                .map(u -> mapper.map(u, UsuarioResponseDTO.class))
                .collect(Collectors.toList());
    }
}
