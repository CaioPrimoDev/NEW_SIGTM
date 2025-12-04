package br.com.ifba.solicitacao.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.solicitacao.dto.SolicitacaoCadastroDTO;
import br.com.ifba.solicitacao.dto.SolicitacaoResponseDTO;
import br.com.ifba.solicitacao.entity.Solicitacao;
import br.com.ifba.solicitacao.service.SolicitacaoIService;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.user.UsuarioIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoIService service;
    private final UsuarioIService usuarioService; // Necessário para buscar o usuário pelo ID
    private final ObjectMapperUtill mapper;

    // 1. CRIAR SOLICITAÇÃO
    @PostMapping("/save")
    public ResponseEntity<SolicitacaoResponseDTO> salvar(@RequestBody SolicitacaoCadastroDTO dto) {
        // 1. Busca o Usuário (Garante que existe, senão lança RegraNegocioException)
        Usuario usuario = usuarioService.findById(dto.getUsuarioId());

        // 2. Mapeia DTO -> Entidade
        Solicitacao solicitacao = mapper.map(dto, Solicitacao.class);

        // 3. Força o vínculo da entidade Usuário (caso o mapper não faça automaticamente pelo ID)
        solicitacao.setUsuario(usuario);

        // 4. Salva (O Service força solicitouParceria = false inicialmente)
        Solicitacao saved = service.save(solicitacao);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(saved, SolicitacaoResponseDTO.class));
    }

    // 2. DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 3. LISTAR TODAS (Geral)
    @GetMapping("/findall")
    public ResponseEntity<List<SolicitacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(mapListToDto(service.findAll()));
    }

    // 4. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        Solicitacao solicitacao = service.findById(id);
        return ResponseEntity.ok(mapper.map(solicitacao, SolicitacaoResponseDTO.class));
    }

    // 5. BUSCAR POR USUÁRIO (Específico)
    // Endpoint: GET /solicitacoes/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<SolicitacaoResponseDTO> buscarPorUsuario(@PathVariable Long usuarioId) {
        // Busca a entidade Usuário primeiro
        Usuario usuario = usuarioService.findById(usuarioId);

        // O Service retorna um Optional<Solicitacao>
        return service.findByUsuario(usuario)
                .map(s -> ResponseEntity.ok(mapper.map(s, SolicitacaoResponseDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    // 6. LISTAR APENAS AS "ATIVAS" (SolicitouParceria = true)

    // Endpoint: GET /solicitacoes/ativas
    @GetMapping("/ativas")
    public ResponseEntity<List<SolicitacaoResponseDTO>> listarSolicitacoesAtivas() {
        return ResponseEntity.ok(mapListToDto(service.findBySolicitouParceriaTrue()));
    }

    // 7. BUSCAR POR NOME DO USUÁRIO (COM SOLICITAÇÃO ATIVA)
    // Endpoint: GET /solicitacoes/buscar?nome=Joao
    @GetMapping("/buscar")
    public ResponseEntity<List<SolicitacaoResponseDTO>> buscarPorNomeUsuario(@RequestParam("nome") String nome) {
        return ResponseEntity.ok(mapListToDto(service.findByNomeUsuarioComSolicitacaoAtiva(nome)));
    }

    // --- Auxiliar ---
    private List<SolicitacaoResponseDTO> mapListToDto(List<Solicitacao> lista) {
        return lista.stream()
                .map(s -> mapper.map(s, SolicitacaoResponseDTO.class))
                .collect(Collectors.toList());
    }
}
