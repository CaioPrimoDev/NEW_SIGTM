package br.com.ifba.promocao.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.promocao.dto.promo.PromocaoDTO;
import br.com.ifba.promocao.dto.promo.PromocaoResponseDTO;
import br.com.ifba.promocao.entity.Promocao;
import br.com.ifba.promocao.entity.TipoPromocao;
import br.com.ifba.promocao.service.promo.PromocaoIService;
import br.com.ifba.promocao.service.tipo_promocao.TipoPromocaoIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/promocoes")
@RequiredArgsConstructor
public class PromocaoController {

    private final PromocaoIService service;
    private final TipoPromocaoIService tipoService; // Necessário para converter ID em Entidade
    private final ObjectMapperUtill mapper;

    // 1. SALVAR (Create)
    @PostMapping("/save")
    public ResponseEntity<PromocaoResponseDTO> salvar(@RequestBody PromocaoDTO dto) {

        // 1. Busca o TipoPromocao pelo ID (lança exceção se não achar)
        TipoPromocao tipo = tipoService.findById(dto.getTipoPromocaoId());

        // 2. Mapeia DTO -> Entidade
        Promocao entity = mapper.map(dto, Promocao.class);

        // 3. Vincula o Tipo (Service já cuida do UsuarioCriador via sessão)
        entity.setTipo(tipo);

        // 4. Salva (Service executa validatePromocao)
        Promocao saved = service.save(entity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(saved));
    }

    // 2. ATUALIZAR (Update)
    @PutMapping("/{id}")
    public ResponseEntity<PromocaoResponseDTO> atualizar(@PathVariable Long id,
                                                         @RequestBody PromocaoDTO dto) {
        // Busca o Tipo (pode ter mudado)
        TipoPromocao tipo = tipoService.findById(dto.getTipoPromocaoId());

        Promocao entity = mapper.map(dto, Promocao.class);
        entity.setId(id);
        entity.setTipo(tipo);

        Promocao updated = service.update(entity);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    // 3. DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Como o Service espera um objeto 'Promocao' no delete, buscamos primeiro
        Promocao entity = service.findById(id);
        service.delete(entity);
        return ResponseEntity.noContent().build();
    }

    // 4. LISTAR TODAS
    @GetMapping("/findall")
    public ResponseEntity<List<PromocaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // 5. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<PromocaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    // Endpoint: GET /promocoes/filtrar?termo=Natal&tipo=Desconto
    @GetMapping("/filtrar")
    public ResponseEntity<List<PromocaoResponseDTO>> filtrar(@RequestParam(required = false) String termo,
                                                             @RequestParam(defaultValue = "TODOS") String tipo) {
        // Chama o método específico do service
        List<Promocao> filtradas = service.filtrarPromocoes(termo, tipo);

        return ResponseEntity.ok(
                filtradas.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // --- Auxiliar de Mapeamento ---
    private PromocaoResponseDTO mapToResponse(Promocao entity) {
        PromocaoResponseDTO dto = mapper.map(entity, PromocaoResponseDTO.class);

        // Preenche campos de visualização rápida
        if (entity.getUsuarioCriador() != null && entity.getUsuarioCriador().getPessoa() != null) {
            dto.setNomeUsuarioCriador(entity.getUsuarioCriador().getPessoa().getNome());
        }
        if (entity.getTipo() != null) {
            dto.setTituloTipoPromocao(entity.getTipo().getTitulo());
        }

        return dto;
    }
}
