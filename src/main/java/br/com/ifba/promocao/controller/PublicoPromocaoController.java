package br.com.ifba.promocao.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.promocao.dto.publico_promo.PublicoPromocaoDTO;
import br.com.ifba.promocao.dto.publico_promo.PublicoPromocaoResponseDTO;
import br.com.ifba.promocao.entity.PublicoPromocao;
import br.com.ifba.promocao.service.publico_promo.PublicoPromocaoIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/publicos-promocao", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PublicoPromocaoController {

    private final PublicoPromocaoIService service;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PublicoPromocaoResponseDTO> salvar(@RequestBody @Valid PublicoPromocaoDTO dto) {
        PublicoPromocao entity = mapper.map(dto, PublicoPromocao.class);
        PublicoPromocao saved = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(saved));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PublicoPromocaoResponseDTO> atualizar(@PathVariable Long id,
                                                                @RequestBody @Valid PublicoPromocaoDTO dto) {
        PublicoPromocao entity = mapper.map(dto, PublicoPromocao.class);
        entity.setId(id);
        PublicoPromocao updated = service.update(entity);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<PublicoPromocaoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<PublicoPromocaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    private PublicoPromocaoResponseDTO mapToResponse(PublicoPromocao entity) {
        PublicoPromocaoResponseDTO dto = mapper.map(entity, PublicoPromocaoResponseDTO.class);
        if (entity.getUsuarioCadastro() != null && entity.getUsuarioCadastro().getPessoa() != null) {
            dto.setNomeUsuarioCadastro(entity.getUsuarioCadastro().getPessoa().getNome());
        }
        return dto;
    }
}
