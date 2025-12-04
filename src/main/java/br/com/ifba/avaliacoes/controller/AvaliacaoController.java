package br.com.ifba.avaliacoes.controller;

import br.com.ifba.avaliacoes.dto.AvaliacaoDTO;
import br.com.ifba.avaliacoes.dto.AvaliacaoResponseDTO;
import br.com.ifba.avaliacoes.entity.Avaliacao;
import br.com.ifba.avaliacoes.service.AvaliacaoIService;
import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.sessao.entity.UsuarioSession;
import br.com.ifba.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoIService service;
    private final UsuarioSession usuarioSession; // Necessário para identificar o autor
    private final ObjectMapperUtill mapper;

    // =========================================================
    // ROTAS VINCULADAS AO PONTO TURÍSTICO (Sub-recursos)
    // =========================================================

    // 1. CRIAR AVALIAÇÃO
    // POST /pontos-turisticos/{pontoId}/avaliacoes
    @PostMapping("/pontos-turisticos/{pontoId}/avaliacoes")
    public ResponseEntity<AvaliacaoResponseDTO> avaliarPonto(@PathVariable Long pontoId,
                                                             @RequestBody AvaliacaoDTO dto) {

        // 1. Verifica Login
        if (!usuarioSession.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuarioLogado = usuarioSession.getUsuarioLogado();

        // 2. Prepara a Entidade
        Avaliacao entity = new Avaliacao();
        entity.setEstrelas(dto.getEstrelas());
        entity.setDescricao(dto.getDescricao());

        // 3. Vincula o Usuário (Importante para a regra de negócio e nomeAutor)
        entity.setUsuario(usuarioLogado);
        if (usuarioLogado.getPessoa() != null) {
            entity.setNomeAutor(usuarioLogado.getPessoa().getNome());
        }

        // 4. Salva (Service vincula o Ponto e valida)
        Avaliacao saved = service.saveForPonto(pontoId, entity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(saved));
    }

    // 2. LISTAR TODAS DO PONTO
    // GET /pontos-turisticos/{pontoId}/avaliacoes
    @GetMapping("/pontos-turisticos/{pontoId}/avaliacoes")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorPonto(@PathVariable Long pontoId) {
        return ResponseEntity.ok(
                service.findAllByPonto(pontoId).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // 3. LISTAR MELHORES DO PONTO
    // GET /pontos-turisticos/{pontoId}/avaliacoes/melhores
    @GetMapping("/pontos-turisticos/{pontoId}/avaliacoes/melhores")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarMelhores(@PathVariable Long pontoId) {
        return ResponseEntity.ok(
                service.getMelhoresByPonto(pontoId).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // 4. LISTAR PIORES DO PONTO
    // GET /pontos-turisticos/{pontoId}/avaliacoes/piores
    @GetMapping("/pontos-turisticos/{pontoId}/avaliacoes/piores")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPiores(@PathVariable Long pontoId) {
        return ResponseEntity.ok(
                service.getPioresByPonto(pontoId).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // =========================================================
    // ROTAS GERAIS DE AVALIAÇÃO
    // =========================================================

    // 5. ATUALIZAR (O ID aqui é da Avaliação, não do ponto)
    // PUT /avaliacoes/{id}
    @PutMapping("/avaliacoes/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> atualizar(@PathVariable Long id,
                                                          @RequestBody AvaliacaoDTO dto) {
        // Validação de segurança: Idealmente verificar se o usuário logado é o dono da avaliação
        if (!usuarioSession.isLogado()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Avaliacao entity = new Avaliacao();
        entity.setEstrelas(dto.getEstrelas());
        entity.setDescricao(dto.getDescricao());
        // Se o nome do autor puder mudar, setar aqui, senão o service mantém o antigo.
        entity.setNomeAutor(usuarioSession.getUsuarioLogado().getPessoa().getNome());

        Avaliacao updated = service.update(id, entity);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    // 6. DELETAR
    // DELETE /avaliacoes/{id}
    @DeleteMapping("/avaliacoes/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Validação de segurança: Verificar se é dono ou admin antes
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 7. BUSCAR POR ID
    @GetMapping("/avaliacoes/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    // 8. MINHAS AVALIAÇÕES (Usuário Logado)
    @GetMapping("/avaliacoes/me")
    public ResponseEntity<List<AvaliacaoResponseDTO>> minhasAvaliacoes() {
        if (!usuarioSession.isLogado()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Long usuarioId = usuarioSession.getUsuarioLogado().getId();

        return ResponseEntity.ok(
                service.findByUsuarioId(usuarioId).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // --- Auxiliar ---
    private AvaliacaoResponseDTO mapToResponse(Avaliacao entity) {
        AvaliacaoResponseDTO dto = mapper.map(entity, AvaliacaoResponseDTO.class);

        if (entity.getPontoTuristico() != null) {
            dto.setPontoTuristicoId(entity.getPontoTuristico().getId());
            dto.setNomePontoTuristico(entity.getPontoTuristico().getNome());
        }

        return dto;
    }
}
