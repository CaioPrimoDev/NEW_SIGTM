package br.com.ifba.pessoa.other_users.usuariocomum.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.pessoa.other_users.usuariocomum.dto.UsuarioComumCadastroDTO;
import br.com.ifba.pessoa.other_users.usuariocomum.dto.UsuarioComumResponseDTO;
import br.com.ifba.pessoa.other_users.usuariocomum.entity.UsuarioComum;
import br.com.ifba.pessoa.other_users.usuariocomum.service.UsuarioComumIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios-comuns")
@RequiredArgsConstructor
public class UsuarioComumController {

    private final UsuarioComumIService service;
    private final ObjectMapperUtill mapper;

    @PostMapping("/save")
    public ResponseEntity<UsuarioComumResponseDTO> salvar(@RequestBody UsuarioComumCadastroDTO dto) {
        UsuarioComum usuario = mapper.map(dto, UsuarioComum.class);

        service.save(usuario);

        UsuarioComumResponseDTO response = mapper.map(usuario, UsuarioComumResponseDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/findall")
    public ResponseEntity<List<UsuarioComumResponseDTO>> listarTodos() {
        List<UsuarioComum> usuarios = service.findAll();

        return ResponseEntity.ok(mapListToDto(usuarios));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioComumResponseDTO> buscarPorId(@PathVariable Long id) {
        // O service lança exceção se não achar, então o retorno aqui é garantido
        UsuarioComum usuario = service.findById(id);

        return ResponseEntity.ok(mapper.map(usuario, UsuarioComumResponseDTO.class));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioComumResponseDTO>> buscarPorNome(@RequestParam("nome") String nome) {
        List<UsuarioComum> usuarios = service.findByNomeContainingIgnoreCase(nome);

        return ResponseEntity.ok(mapListToDto(usuarios));
    }

    private List<UsuarioComumResponseDTO> mapListToDto(List<UsuarioComum> lista) {
        return lista.stream()
                .map(u -> mapper.map(u, UsuarioComumResponseDTO.class))
                .collect(Collectors.toList());
    }
}