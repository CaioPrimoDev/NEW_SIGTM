package br.com.ifba.usuario.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.usuario.dto.tipo_user.TipoUsuarioCadastroDTO;
import br.com.ifba.usuario.dto.tipo_user.TipoUsuarioResponseDTO;
import br.com.ifba.usuario.entity.TipoUsuario;
import br.com.ifba.usuario.service.tipo_user.TipoUsuarioIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/tipos-usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TipoUsuarioController {

    private final TipoUsuarioIService service;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TipoUsuarioResponseDTO> salvar(@RequestBody @Valid TipoUsuarioCadastroDTO dto) {
        TipoUsuario entity = mapper.map(dto, TipoUsuario.class);
        service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(entity, TipoUsuarioResponseDTO.class));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<TipoUsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(mapListToDto(service.findAll()));
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<TipoUsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        TipoUsuario entity = service.findById(id);
        return ResponseEntity.ok(mapper.map(entity, TipoUsuarioResponseDTO.class));
    }

    @GetMapping(value = "/find/nome") // Alterado para /find/nome para manter padrão
    public ResponseEntity<TipoUsuarioResponseDTO> buscarPorNome(@RequestParam("nome") String nome) {
        TipoUsuario entity = service.findByNome(nome);
        if (entity == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.map(entity, TipoUsuarioResponseDTO.class));
    }

    private List<TipoUsuarioResponseDTO> mapListToDto(List<TipoUsuario> lista) {
        return lista.stream()
                .map(item -> mapper.map(item, TipoUsuarioResponseDTO.class))
                .collect(Collectors.toList());
    }
}
