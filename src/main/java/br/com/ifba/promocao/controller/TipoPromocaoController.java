package br.com.ifba.promocao.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.promocao.dto.tipo_promocao.TipoPromocaoDTO;
import br.com.ifba.promocao.dto.tipo_promocao.TipoPromocaoResponseDTO;
import br.com.ifba.promocao.entity.PublicoPromocao;
import br.com.ifba.promocao.entity.TipoPromocao;
import br.com.ifba.promocao.service.publico_promo.PublicoPromocaoIService;
import br.com.ifba.promocao.service.tipo_promocao.TipoPromocaoIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tipos-promocao")
@RequiredArgsConstructor
public class TipoPromocaoController {

    private final TipoPromocaoIService service;
    private final PublicoPromocaoIService publicoService; // Necessário para buscar o público pelo ID
    private final ObjectMapperUtill mapper;

    // 1. SALVAR (Create)
    @PostMapping("/save")
    public ResponseEntity<TipoPromocaoResponseDTO> salvar(@RequestBody TipoPromocaoDTO dto) {

        // 1. Busca a entidade PublicoPromocao pelo ID fornecido
        // Se não existir, o service deve lançar EntityNotFoundException
        PublicoPromocao publico = publicoService.findById(dto.getPublicoAlvoId());

        // 2. Mapeia DTO -> Entidade
        TipoPromocao entity = mapper.map(dto, TipoPromocao.class);

        // 3. Vincula o relacionamento manualmente (para garantir)
        entity.setPublicoAlvo(publico);

        // 4. Salva (Service cuida da segurança/usuário logado)
        TipoPromocao saved = service.save(entity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(saved));
    }

    // 2. ATUALIZAR (Update)
    @PutMapping("/{id}")
    public ResponseEntity<TipoPromocaoResponseDTO> atualizar(@PathVariable Long id,
                                                             @RequestBody TipoPromocaoDTO dto) {
        // Busca o público alvo (pode ter mudado na edição)
        PublicoPromocao publico = publicoService.findById(dto.getPublicoAlvoId());

        TipoPromocao entity = mapper.map(dto, TipoPromocao.class);
        entity.setId(id);
        entity.setPublicoAlvo(publico);

        // Service valida permissões de edição
        TipoPromocao updated = service.update(entity);

        return ResponseEntity.ok(mapToResponse(updated));
    }

    // 3. DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Service valida permissões de exclusão
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 4. LISTAR TODOS
    @GetMapping("/findall")
    public ResponseEntity<List<TipoPromocaoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // 5. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<TipoPromocaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    // --- Auxiliar para mapeamento manual de campos aninhados ---
    private TipoPromocaoResponseDTO mapToResponse(TipoPromocao entity) {
        TipoPromocaoResponseDTO dto = mapper.map(entity, TipoPromocaoResponseDTO.class);

        // Mapeia nome do criador
        if (entity.getUsuarioCadastro() != null && entity.getUsuarioCadastro().getPessoa() != null) {
            dto.setNomeUsuarioCadastro(entity.getUsuarioCadastro().getPessoa().getNome());
        }

        /*
         O PublicoAlvo deve ser mapeado automaticamente pelo ModelMapper
          se os nomes baterem, mas se necessário, pode-se forçar aqui:
          dto.setPublicoAlvo(mapper.map(entity.getPublicoAlvo(), PublicoPromocaoResponseDTO.class));
         */

        return dto;
    }
}
