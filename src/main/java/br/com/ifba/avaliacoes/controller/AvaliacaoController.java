package br.com.ifba.avaliacoes.controller;

import br.com.ifba.avaliacoes.dto.AvaliacaoDTO;
import br.com.ifba.avaliacoes.dto.AvaliacaoResponseDTO;
import br.com.ifba.avaliacoes.entity.Avaliacao;
import br.com.ifba.avaliacoes.service.AvaliacaoIService;
import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.sessao.entity.UsuarioSession;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoIService service;
    private final UsuarioSession usuarioSession;
    private final ObjectMapperUtill mapper;

    // ==================================================================================
    // 1. SALVAR AVALIAÇÃO DE PONTO TURÍSTICO
    // ==================================================================================
    @PostMapping(
            value = "/pontos-turisticos/{pontoId}/avaliacoes/save",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AvaliacaoResponseDTO> avaliarPonto(
            @PathVariable Long pontoId,
            @RequestBody @Valid AvaliacaoDTO dto) {

        if (!usuarioSession.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuarioLogado = usuarioSession.getUsuarioLogado();

        Avaliacao entity = new Avaliacao();
        entity.setEstrelas(dto.getEstrelas());
        entity.setDescricao(dto.getDescricao());
        entity.setUsuario(usuarioLogado);

        if (usuarioLogado.getPessoa() != null) {
            entity.setNomeAutor(usuarioLogado.getPessoa().getNome());
        }

        Avaliacao saved = service.saveForPonto(pontoId, entity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapToResponse(saved));
    }

    // ==================================================================================
    // 2. SALVAR AVALIAÇÃO DE EVENTO
    // ==================================================================================
    @PostMapping(
            value = "/eventos/{eventoId}/avaliacoes/save",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AvaliacaoResponseDTO> avaliarEvento(
            @PathVariable Long eventoId,
            @RequestBody @Valid AvaliacaoDTO dto) {

        if (!usuarioSession.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuarioLogado = usuarioSession.getUsuarioLogado();

        Avaliacao entity = new Avaliacao();
        entity.setEstrelas(dto.getEstrelas());
        entity.setDescricao(dto.getDescricao());
        entity.setUsuario(usuarioLogado);

        if (usuarioLogado.getPessoa() != null) {
            entity.setNomeAutor(usuarioLogado.getPessoa().getNome());
        }

        Avaliacao saved = service.saveForEvento(eventoId, entity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapToResponse(saved));
    }

    // ==================================================================================
    // 3. LISTAR AVALIAÇÕES DE UM PONTO ESPECÍFICO (PÚBLICO)
    // ==================================================================================
    @GetMapping("/pontos-turisticos/{pontoId}/avaliacoes")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorPonto(@PathVariable Long pontoId) {
        // Busca todas as avaliações daquele ponto
        List<Avaliacao> lista = service.findAllByPonto(pontoId);

        return ResponseEntity.ok(
                lista.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // ==================================================================================
    // 4. LISTAR AVALIAÇÕES DE UM EVENTO ESPECÍFICO (PÚBLICO) - [NOVO]
    // ==================================================================================
    @GetMapping("/eventos/{eventoId}/avaliacoes")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorEvento(@PathVariable Long eventoId) {
        // Busca todas as avaliações daquele evento
        List<Avaliacao> lista = service.findAllByEvento(eventoId);

        return ResponseEntity.ok(
                lista.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // ==================================================================================
    // 5. LISTAR MINHAS AVALIAÇÕES (USUÁRIO LOGADO)
    // ==================================================================================
    @GetMapping("/avaliacoes/find/me")
    public ResponseEntity<List<AvaliacaoResponseDTO>> minhasAvaliacoes() {

        if (!usuarioSession.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long usuarioId = usuarioSession.getUsuarioLogado().getId();

        return ResponseEntity.ok(
                service.findByUsuarioId(usuarioId)
                        .stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // ==================================================================================
    // MÉTODOS AUXILIARES
    // ==================================================================================
    private AvaliacaoResponseDTO mapToResponse(Avaliacao entity) {
        AvaliacaoResponseDTO dto = mapper.map(entity, AvaliacaoResponseDTO.class);

        // Mapeamento de Ponto Turístico
        if (entity.getPontoTuristico() != null) {
            dto.setPontoTuristicoId(entity.getPontoTuristico().getId());
            dto.setNomePontoTuristico(entity.getPontoTuristico().getNome());
        }

        // Mapeamento de Evento
        if (entity.getEvento() != null) {
            dto.setEventoId(entity.getEvento().getId());
            dto.setNomeEvento(entity.getEvento().getNome());
        }

        return dto;
    }
}