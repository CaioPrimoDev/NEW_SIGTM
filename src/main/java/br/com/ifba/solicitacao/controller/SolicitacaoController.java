package br.com.ifba.solicitacao.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.solicitacao.dto.SolicitacaoCadastroDTO;
import br.com.ifba.solicitacao.dto.SolicitacaoResponseDTO;
import br.com.ifba.solicitacao.entity.Solicitacao;
import br.com.ifba.solicitacao.service.SolicitacaoIService;
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
@RequestMapping(value = "/solicitacoes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoIService service;
    private final UsuarioIService usuarioService;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitacaoResponseDTO> salvar(@RequestBody @Valid SolicitacaoCadastroDTO dto) {
        Usuario usuario = usuarioService.findById(dto.getUsuarioId());

        Solicitacao solicitacao = mapper.map(dto, Solicitacao.class);
        solicitacao.setUsuario(usuario);

        Solicitacao saved = service.save(solicitacao);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(saved, SolicitacaoResponseDTO.class));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<SolicitacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(mapListToDto(service.findAll()));
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<SolicitacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        Solicitacao solicitacao = service.findById(id);
        return ResponseEntity.ok(mapper.map(solicitacao, SolicitacaoResponseDTO.class));
    }

    @GetMapping(value = "/find/usuario/{usuarioId}")
    public ResponseEntity<SolicitacaoResponseDTO> buscarPorUsuario(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioService.findById(usuarioId);
        return service.findByUsuario(usuario)
                .map(s -> ResponseEntity.ok(mapper.map(s, SolicitacaoResponseDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/find/ativas")
    public ResponseEntity<List<SolicitacaoResponseDTO>> listarSolicitacoesAtivas() {
        return ResponseEntity.ok(mapListToDto(service.findBySolicitouParceriaTrue()));
    }

    @GetMapping(value = "/find/buscar") // Mantive buscar pois é query param
    public ResponseEntity<List<SolicitacaoResponseDTO>> buscarPorNomeUsuario(@RequestParam("nome") String nome) {
        return ResponseEntity.ok(mapListToDto(service.findByNomeUsuarioComSolicitacaoAtiva(nome)));
    }

    private List<SolicitacaoResponseDTO> mapListToDto(List<Solicitacao> lista) {
        return lista.stream()
                .map(s -> mapper.map(s, SolicitacaoResponseDTO.class))
                .collect(Collectors.toList());
    }
}
