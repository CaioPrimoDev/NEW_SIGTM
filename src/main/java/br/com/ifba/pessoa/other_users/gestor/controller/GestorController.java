package br.com.ifba.pessoa.other_users.gestor.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.pessoa.other_users.gestor.dto.GestorCadastroDTO;
import br.com.ifba.pessoa.other_users.gestor.dto.GestorResponseDTO;
import br.com.ifba.pessoa.other_users.gestor.entity.Gestor;
import br.com.ifba.pessoa.other_users.gestor.service.GestorIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/gestores", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GestorController {

    private final GestorIService service;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GestorResponseDTO> salvar(@RequestBody @Valid GestorCadastroDTO dto) {
        Gestor gestor = mapper.map(dto, Gestor.class);
        service.save(gestor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(gestor, GestorResponseDTO.class));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<GestorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(mapListToDto(service.findAll()));
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<GestorResponseDTO> buscarPorId(@PathVariable Long id) {
        Gestor gestor = service.findById(id);
        return ResponseEntity.ok(mapper.map(gestor, GestorResponseDTO.class));
    }

    @GetMapping(value = "/find/buscar")
    public ResponseEntity<List<GestorResponseDTO>> buscarPorNome(@RequestParam("nome") String nome) {
        return ResponseEntity.ok(mapListToDto(service.findByNomeContainingIgnoreCase(nome)));
    }

    private List<GestorResponseDTO> mapListToDto(List<Gestor> lista) {
        return lista.stream()
                .map(g -> mapper.map(g, GestorResponseDTO.class))
                .collect(Collectors.toList());
    }
}
