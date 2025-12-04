package br.com.ifba.pontoturistico.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.pessoa.other_users.gestor.entity.Gestor;
import br.com.ifba.pontoturistico.dto.PontoTuristicoDTO;
import br.com.ifba.pontoturistico.dto.PontoTuristicoResponseDTO;
import br.com.ifba.pontoturistico.entity.PontoTuristico;
import br.com.ifba.pontoturistico.service.PontoTuristicoIService;
import br.com.ifba.sessao.entity.UsuarioSession;
import br.com.ifba.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pontos-turisticos")
@RequiredArgsConstructor
public class PontoTuristicoController {

    private final PontoTuristicoIService service;
    private final UsuarioSession usuarioSession; // Para pegar o gestor logado
    private final ObjectMapperUtill mapper;

    // 1. SALVAR (Create)
    @PostMapping("/save")
    public ResponseEntity<PontoTuristicoResponseDTO> salvar(@RequestBody PontoTuristicoDTO dto) {

        // 1. Mapeia DTO -> Entidade (Preenche nome, descr, endereco, horarios)
        PontoTuristico entity = mapper.map(dto, PontoTuristico.class);

        // 2. Vincula o Gestor Logado (Service valida se é GESTOR, mas precisamos setar o objeto)
        if (usuarioSession.isLogado()) {
            Usuario usuario = usuarioSession.getUsuarioLogado();
            // Assumindo que o usuário logado é de fato um Gestor (Service vai validar depois)
            if (usuario.getPessoa() instanceof Gestor) {
                entity.setGestor((Gestor) usuario.getPessoa());
            }
        }

        // 3. Salva (Service cuida do Endereço e Validações)
        service.save(entity); // O método save é void, mas atualiza o ID da entidade

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(entity));
    }

    // 2. ATUALIZAR (Update)
    @PutMapping("/{id}")
    public ResponseEntity<PontoTuristicoResponseDTO> atualizar(@PathVariable Long id,
                                                               @RequestBody PontoTuristicoDTO dto) {
        PontoTuristico entity = mapper.map(dto, PontoTuristico.class);
        entity.setId(id);

        // Precisamos manter o gestor original ou atualizar para o logado?
        // Geralmente mantém-se o original ou o service cuida disso.
        // Vou assumir que o service atualiza os dados mas mantém o gestor se não for setado,
        // mas por segurança, setamos o logado novamente.
        if (usuarioSession.isLogado() && usuarioSession.getUsuarioLogado().getPessoa() instanceof Gestor) {
            entity.setGestor((Gestor) usuarioSession.getUsuarioLogado().getPessoa());
        }

        service.update(entity);

        // Como o update é void e pode ter mudado o endereço (ID),
        // buscamos o atualizado para retornar
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    // 3. DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // O Service pede a entidade para deletar
        PontoTuristico entity = service.findById(id);
        service.delete(entity);
        return ResponseEntity.noContent().build();
    }

    // 4. LISTAR TODOS
    @GetMapping("/findall")
    public ResponseEntity<List<PontoTuristicoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // 5. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<PontoTuristicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    // 6. BUSCAR POR NOME (Autocomplete)
    @GetMapping("/buscar")
    public ResponseEntity<List<PontoTuristicoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(
                service.findByNomeStartingWithIgnoreCase(nome).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // --- Auxiliar ---
    private PontoTuristicoResponseDTO mapToResponse(PontoTuristico entity) {
        PontoTuristicoResponseDTO dto = mapper.map(entity, PontoTuristicoResponseDTO.class);

        // Preenche nome do gestor
        if (entity.getGestor() != null) {
            dto.setNomeGestorResponsavel(entity.getGestor().getNome());
        }

        return dto;
    }
}