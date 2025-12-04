package br.com.ifba.pessoa.other_users.gestor.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.pessoa.other_users.gestor.dto.GestorCadastroDTO;
import br.com.ifba.pessoa.other_users.gestor.dto.GestorResponseDTO;
import br.com.ifba.pessoa.other_users.gestor.entity.Gestor;
import br.com.ifba.pessoa.other_users.gestor.service.GestorIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gestores")
@RequiredArgsConstructor
public class GestorController {

    private final GestorIService service;
    private final ObjectMapperUtill mapper;

    @PostMapping("/save")
    public ResponseEntity<GestorResponseDTO> salvar(@RequestBody GestorCadastroDTO dto) {
        Gestor gestor = mapper.map(dto, Gestor.class);

        service.save(gestor);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(gestor, GestorResponseDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/findall")
    public ResponseEntity<List<GestorResponseDTO>> listarTodos() {
        List<Gestor> gestores = service.findAll();

        return ResponseEntity.ok(mapListToDto(gestores));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GestorResponseDTO> buscarPorId(@PathVariable Long id) {
        // O service lança exceção se não encontrar, então não precisamos de if/else aqui
        Gestor gestor = service.findById(id);

        return ResponseEntity.ok(mapper.map(gestor, GestorResponseDTO.class));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<GestorResponseDTO>> buscarPorNome(@RequestParam("nome") String nome) {
        List<Gestor> gestores = service.findByNomeContainingIgnoreCase(nome);

        return ResponseEntity.ok(mapListToDto(gestores));
    }
    /**
     * Converte uma lista de Entidades Gestor para uma lista de GestorResponseDTO.
     * Útil para evitar repetição de código nos métodos de listagem.
     */
    private List<GestorResponseDTO> mapListToDto(List<Gestor> lista) {
        return lista.stream()
                .map(g -> mapper.map(g, GestorResponseDTO.class))
                .collect(Collectors.toList());
    }
}
