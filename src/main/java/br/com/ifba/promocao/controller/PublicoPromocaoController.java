package br.com.ifba.promocao.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.promocao.dto.publico_promo.PublicoPromocaoDTO;
import br.com.ifba.promocao.dto.publico_promo.PublicoPromocaoResponseDTO;
import br.com.ifba.promocao.entity.PublicoPromocao;
import br.com.ifba.promocao.service.publico_promo.PublicoPromocaoIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/publicos-promocao")
@RequiredArgsConstructor
public class PublicoPromocaoController {

    private final PublicoPromocaoIService service;
    private final ObjectMapperUtill mapper;

    // 1. SALVAR (Create)
    @PostMapping("/save")
    public ResponseEntity<PublicoPromocaoResponseDTO> salvar(@RequestBody PublicoPromocaoDTO dto) {
        // Mapeia DTO -> Entidade
        PublicoPromocao entity = mapper.map(dto, PublicoPromocao.class);

        // O Service:
        // 1. Verifica login
        // 2. Verifica permissão (Parceiro/Gestor)
        // 3. Vincula o usuário logado
        // 4. Salva
        PublicoPromocao saved = service.save(entity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(saved));
    }

    // 2. ATUALIZAR (Update)
    @PutMapping("/{id}")
    public ResponseEntity<PublicoPromocaoResponseDTO> atualizar(@PathVariable Long id,
                                                                @RequestBody PublicoPromocaoDTO dto) {
        // Busca o ID para garantir que estamos editando o correto
        PublicoPromocao entity = mapper.map(dto, PublicoPromocao.class);
        entity.setId(id); // Força o ID da URL na entidade

        // O Service valida se o usuário logado é dono ou gestor
        PublicoPromocao updated = service.update(entity);

        return ResponseEntity.ok(mapToResponse(updated));
    }

    // 3. DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // O Service valida permissão antes de deletar
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 4. LISTAR TODOS
    @GetMapping("/findall")
    public ResponseEntity<List<PublicoPromocaoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // 5. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<PublicoPromocaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    // --- Mapeamento Manual Auxiliar ---
    // (Útil para preencher o nome do usuário cadastro que está aninhado)
    private PublicoPromocaoResponseDTO mapToResponse(PublicoPromocao entity) {
        PublicoPromocaoResponseDTO dto = mapper.map(entity, PublicoPromocaoResponseDTO.class);

        if (entity.getUsuarioCadastro() != null && entity.getUsuarioCadastro().getPessoa() != null) {
            dto.setNomeUsuarioCadastro(entity.getUsuarioCadastro().getPessoa().getNome());
        }

        return dto;
    }
}
