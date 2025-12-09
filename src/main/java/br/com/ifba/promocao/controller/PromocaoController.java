package br.com.ifba.promocao.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.promocao.dto.promo.PromocaoDTO;
import br.com.ifba.promocao.dto.promo.PromocaoResponseDTO;
import br.com.ifba.promocao.entity.Promocao;
import br.com.ifba.promocao.entity.TipoPromocao;
import br.com.ifba.promocao.service.promo.PromocaoIService;
import br.com.ifba.promocao.service.tipo_promocao.TipoPromocaoIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/promocoes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PromocaoController {

    private final PromocaoIService service;
    private final TipoPromocaoIService tipoService;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PromocaoResponseDTO> salvar(@RequestBody @Valid PromocaoDTO dto) {
        TipoPromocao tipo = tipoService.findById(dto.getTipoPromocaoId());

        Promocao entity = mapper.map(dto, Promocao.class);
        entity.setTipo(tipo);

        Promocao saved = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(saved));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PromocaoResponseDTO> atualizar(@PathVariable Long id,
                                                         @RequestBody @Valid PromocaoDTO dto) {
        TipoPromocao tipo = tipoService.findById(dto.getTipoPromocaoId());

        Promocao entity = mapper.map(dto, Promocao.class);
        entity.setId(id);
        entity.setTipo(tipo);

        Promocao updated = service.update(entity);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Promocao entity = service.findById(id);
        service.delete(entity);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<PromocaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<PromocaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    @GetMapping(value = "/find/filtrar")
    public ResponseEntity<List<PromocaoResponseDTO>> filtrar(@RequestParam(required = false) String termo,
                                                             @RequestParam(defaultValue = "TODOS") String tipo) {
        List<Promocao> filtradas = service.filtrarPromocoes(termo, tipo);
        return ResponseEntity.ok(
                filtradas.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    private PromocaoResponseDTO mapToResponse(Promocao entity) {
        PromocaoResponseDTO dto = mapper.map(entity, PromocaoResponseDTO.class);

        if (entity.getUsuarioCriador() != null && entity.getUsuarioCriador().getPessoa() != null) {
            dto.setNomeUsuarioCriador(entity.getUsuarioCriador().getPessoa().getNome());
        }
        if (entity.getTipo() != null) {
            dto.setTituloTipoPromocao(entity.getTipo().getTitulo());
        }
        return dto;
    }
}
