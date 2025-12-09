package br.com.ifba.promocao.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.promocao.dto.tipo_promocao.TipoPromocaoDTO;
import br.com.ifba.promocao.dto.tipo_promocao.TipoPromocaoResponseDTO;
import br.com.ifba.promocao.entity.PublicoPromocao;
import br.com.ifba.promocao.entity.TipoPromocao;
import br.com.ifba.promocao.service.publico_promo.PublicoPromocaoIService;
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
@RequestMapping(value = "/tipos-promocao", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TipoPromocaoController {

    private final TipoPromocaoIService service;
    private final PublicoPromocaoIService publicoService;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TipoPromocaoResponseDTO> salvar(@RequestBody @Valid TipoPromocaoDTO dto) {
        PublicoPromocao publico = publicoService.findById(dto.getPublicoAlvoId());

        TipoPromocao entity = mapper.map(dto, TipoPromocao.class);
        entity.setPublicoAlvo(publico);

        TipoPromocao saved = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(saved));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TipoPromocaoResponseDTO> atualizar(@PathVariable Long id,
                                                             @RequestBody @Valid TipoPromocaoDTO dto) {
        PublicoPromocao publico = publicoService.findById(dto.getPublicoAlvoId());

        TipoPromocao entity = mapper.map(dto, TipoPromocao.class);
        entity.setId(id);
        entity.setPublicoAlvo(publico);

        TipoPromocao updated = service.update(entity);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<TipoPromocaoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<TipoPromocaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    private TipoPromocaoResponseDTO mapToResponse(TipoPromocao entity) {
        TipoPromocaoResponseDTO dto = mapper.map(entity, TipoPromocaoResponseDTO.class);
        if (entity.getUsuarioCadastro() != null && entity.getUsuarioCadastro().getPessoa() != null) {
            dto.setNomeUsuarioCadastro(entity.getUsuarioCadastro().getPessoa().getNome());
        }
        return dto;
    }
}
