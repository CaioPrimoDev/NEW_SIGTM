package br.com.ifba.evento.controller;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.evento.dto.EventoDTO;
import br.com.ifba.evento.dto.EventoResponseDTO;
import br.com.ifba.evento.entity.Evento;
import br.com.ifba.evento.service.EventoIService;
import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.pessoa.other_users.parceiro.entity.Parceiro;
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
@RequestMapping(value = "/eventos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EventoController {

    private final EventoIService service;
    private final UsuarioSession usuarioSession;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventoResponseDTO> salvar(@RequestBody @Valid EventoDTO dto) {

        if (!usuarioSession.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuario = usuarioSession.getUsuarioLogado();

        if (!(usuario.getPessoa() instanceof Parceiro)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Parceiro parceiroLogado = (Parceiro) usuario.getPessoa();

        Evento evento = mapper.map(dto, Evento.class);
        // Validamos o EnderecoDTO dentro do EventoDTO também
        Endereco endereco = mapper.map(dto.getEndereco(), Endereco.class);

        Evento saved = service.adicionarEvento(evento, parceiroLogado, endereco);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(saved));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventoResponseDTO> atualizar(@PathVariable Long id,
                                                       @RequestBody @Valid EventoDTO dto) {
        if (!usuarioSession.isLogado()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Evento evento = mapper.map(dto, Evento.class);
        evento.setId(id);
        Endereco endereco = mapper.map(dto.getEndereco(), Endereco.class);
        evento.setEndereco(endereco);

        Usuario usuario = usuarioSession.getUsuarioLogado();
        if (usuario.getPessoa() instanceof Parceiro) {
            evento.setParceiro((Parceiro) usuario.getPessoa());
        }

        service.save(evento);
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<EventoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<EventoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    @GetMapping(value = "/find/buscar")
    public ResponseEntity<List<EventoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(
                service.findByNomeContainingIgnoreCase(nome).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping(value = "/find/categoria")
    public ResponseEntity<List<EventoResponseDTO>> buscarPorCategoria(@RequestParam String categoria) {
        return ResponseEntity.ok(
                service.findByCategoriaContainingIgnoreCase(categoria).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    private EventoResponseDTO mapToResponse(Evento entity) {
        EventoResponseDTO dto = mapper.map(entity, EventoResponseDTO.class);
        if (entity.getParceiro() != null) {
            dto.setNomeParceiroResponsavel(entity.getParceiro().getNomeEmpresa());
        }
        return dto;
    }
}
