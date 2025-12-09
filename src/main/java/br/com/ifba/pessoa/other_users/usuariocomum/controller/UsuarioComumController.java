package br.com.ifba.pessoa.other_users.usuariocomum.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.pessoa.other_users.usuariocomum.dto.UsuarioComumCadastroDTO;
import br.com.ifba.pessoa.other_users.usuariocomum.dto.UsuarioComumResponseDTO;
import br.com.ifba.pessoa.other_users.usuariocomum.entity.UsuarioComum;
import br.com.ifba.pessoa.other_users.usuariocomum.service.UsuarioComumIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/usuarios-comuns", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UsuarioComumController {

    private final UsuarioComumIService service;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioComumResponseDTO> salvar(@RequestBody @Valid UsuarioComumCadastroDTO dto) {
        UsuarioComum usuario = mapper.map(dto, UsuarioComum.class);
        service.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(usuario, UsuarioComumResponseDTO.class));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<UsuarioComumResponseDTO>> listarTodos() {
        return ResponseEntity.ok(mapListToDto(service.findAll()));
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<UsuarioComumResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioComum usuario = service.findById(id);
        return ResponseEntity.ok(mapper.map(usuario, UsuarioComumResponseDTO.class));
    }

    @GetMapping(value = "/find/buscar")
    public ResponseEntity<List<UsuarioComumResponseDTO>> buscarPorNome(@RequestParam("nome") String nome) {
        return ResponseEntity.ok(mapListToDto(service.findByNomeContainingIgnoreCase(nome)));
    }

    private List<UsuarioComumResponseDTO> mapListToDto(List<UsuarioComum> lista) {
        return lista.stream()
                .map(u -> mapper.map(u, UsuarioComumResponseDTO.class))
                .collect(Collectors.toList());
    }
}