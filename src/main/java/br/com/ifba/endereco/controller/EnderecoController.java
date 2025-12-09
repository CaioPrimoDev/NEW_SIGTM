package br.com.ifba.endereco.controller;

import br.com.ifba.endereco.dto.EnderecoCadastroDTO;
import br.com.ifba.endereco.dto.EnderecoResponseDTO;
import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.endereco.service.EnderecoIService;
import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/enderecos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoIService service;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnderecoResponseDTO> salvar(@RequestBody @Valid EnderecoCadastroDTO dto) {
        Endereco entity = mapper.map(dto, Endereco.class);
        Endereco saved = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(saved, EnderecoResponseDTO.class));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnderecoResponseDTO> atualizar(@PathVariable Long id,
                                                         @RequestBody @Valid EnderecoCadastroDTO dto) {
        Endereco entity = mapper.map(dto, Endereco.class);
        entity.setId(id);
        Endereco updated = service.update(entity);
        return ResponseEntity.ok(mapper.map(updated, EnderecoResponseDTO.class));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<EnderecoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(e -> mapper.map(e, EnderecoResponseDTO.class))
                        .collect(Collectors.toList())
        );
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<EnderecoResponseDTO> buscarPorId(@PathVariable Long id) {
        Endereco entity = service.findById(id);
        return ResponseEntity.ok(mapper.map(entity, EnderecoResponseDTO.class));
    }

    @PostMapping(value = "/encontrar-ou-criar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnderecoResponseDTO> encontrarOuCriar(@RequestBody @Valid EnderecoCadastroDTO dto) {
        Endereco entity = mapper.map(dto, Endereco.class);
        Endereco result = service.encontrarOuCriarEndereco(entity);
        return ResponseEntity.ok(mapper.map(result, EnderecoResponseDTO.class));
    }
}
