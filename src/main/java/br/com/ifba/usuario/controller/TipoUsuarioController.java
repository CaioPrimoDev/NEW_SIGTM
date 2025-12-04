package br.com.ifba.usuario.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.usuario.dto.tipo_user.TipoUsuarioCadastroDTO;
import br.com.ifba.usuario.dto.tipo_user.TipoUsuarioResponseDTO;
import br.com.ifba.usuario.entity.TipoUsuario;
import br.com.ifba.usuario.service.tipo_user.TipoUsuarioIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tipos-usuarios")
@RequiredArgsConstructor
public class TipoUsuarioController {

    private final TipoUsuarioIService service;
    private final ObjectMapperUtill mapper;

    // 1. SALVAR (Create/Update)
    @PostMapping("/save")
    public ResponseEntity<TipoUsuarioResponseDTO> salvar(@RequestBody TipoUsuarioCadastroDTO dto) {
        // Mapeia DTO -> Entidade
        TipoUsuario entity = mapper.map(dto, TipoUsuario.class);

        // O Service valida e salva. O ID é gerado automaticamente.
        service.save(entity);

        // Retorna 201 Created
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(entity, TipoUsuarioResponseDTO.class));
    }

    // 2. DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 3. LISTAR TODOS
    @GetMapping("/findall")
    public ResponseEntity<List<TipoUsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(mapListToDto(service.findAll()));
    }

    // 4. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<TipoUsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        // O service lança RegraNegocioException se não encontrar
        TipoUsuario entity = service.findById(id);
        return ResponseEntity.ok(mapper.map(entity, TipoUsuarioResponseDTO.class));
    }

    // 5. BUSCAR POR NOME
    // Ex: GET /tipos-usuarios/buscar?nome=ADMIN
    @GetMapping("/buscar")
    public ResponseEntity<TipoUsuarioResponseDTO> buscarPorNome(@RequestParam("nome") String nome) {
        // O Service retorna a entidade ou null (conforme implementação enviada)
        TipoUsuario entity = service.findByNome(nome);

        if (entity == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapper.map(entity, TipoUsuarioResponseDTO.class));
    }

    // --- Auxiliar ---
    private List<TipoUsuarioResponseDTO> mapListToDto(List<TipoUsuario> lista) {
        return lista.stream()
                .map(item -> mapper.map(item, TipoUsuarioResponseDTO.class))
                .collect(Collectors.toList());
    }
}
