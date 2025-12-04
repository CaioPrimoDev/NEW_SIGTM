package br.com.ifba.evento.controller;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.evento.dto.EventoDTO;
import br.com.ifba.evento.dto.EventoResponseDTO;
import br.com.ifba.evento.entity.Evento;
import br.com.ifba.evento.service.EventoIService;
import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.pessoa.other_users.parceiro.entity.Parceiro;
import br.com.ifba.sessao.entity.UsuarioSession;
import br.com.ifba.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoIService service;
    private final UsuarioSession usuarioSession; // Para identificar o Parceiro logado
    private final ObjectMapperUtill mapper;

    // 1. SALVAR (Create)
    @PostMapping("/save")
    public ResponseEntity<EventoResponseDTO> salvar(@RequestBody EventoDTO dto) {

        // 1. Validação de Segurança: O usuário está logado?
        if (!usuarioSession.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioSession.getUsuarioLogado();

        // 2. Validação de Regra: O usuário é um Parceiro?
        if (!(usuario.getPessoa() instanceof Parceiro)) {
            // Apenas Parceiros podem criar eventos
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Parceiro parceiroLogado = (Parceiro) usuario.getPessoa();

        // 3. Mapeamento
        Evento evento = mapper.map(dto, Evento.class);
        Endereco endereco = mapper.map(dto.getEndereco(), Endereco.class);

        // 4. Salva usando o método específico do Service que orquestra tudo
        // Ele salva Endereco -> Evento -> Atualiza Parceiro
        Evento saved = service.adicionarEvento(evento, parceiroLogado, endereco);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(saved));
    }

    // 2. ATUALIZAR (Update)
    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> atualizar(@PathVariable Long id,
                                                       @RequestBody EventoDTO dto) {
        // Validação de segurança básica (Login)
        if (!usuarioSession.isLogado()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // Busca o evento existente para garantir que existe e checar dono (opcional)
        // O service.save() que chamaremos abaixo vai validar os campos
        Evento evento = mapper.map(dto, Evento.class);
        evento.setId(id);

        // Mapeia o endereço também, garantindo que o ID do endereço seja mantido se necessário,
        // ou deixa o Service tratar com o EnderecoService.
        // Como seu service validarEvento checa campos de endereço, garantimos que ele vai populado.
        Endereco endereco = mapper.map(dto.getEndereco(), Endereco.class);
        evento.setEndereco(endereco);

        // Precisamos garantir que o parceiro não seja perdido na atualização
        // Idealmente, buscamos o evento original para manter o parceiro,
        // ou setamos o parceiro logado se for o dono.
        Usuario usuario = usuarioSession.getUsuarioLogado();
        if (usuario.getPessoa() instanceof Parceiro) {
            evento.setParceiro((Parceiro) usuario.getPessoa());
        }

        service.save(evento);

        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    // 3. DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 4. LISTAR TODOS
    @GetMapping("/findall")
    public ResponseEntity<List<EventoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // 5. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    // 6. BUSCAR POR NOME
    @GetMapping("/buscar")
    public ResponseEntity<List<EventoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(
                service.findByNomeContainingIgnoreCase(nome).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // 7. BUSCAR POR CATEGORIA
    @GetMapping("/categoria")
    public ResponseEntity<List<EventoResponseDTO>> buscarPorCategoria(@RequestParam String categoria) {
        return ResponseEntity.ok(
                service.findByCategoriaContainingIgnoreCase(categoria).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // --- Auxiliar ---
    private EventoResponseDTO mapToResponse(Evento entity) {
        EventoResponseDTO dto = mapper.map(entity, EventoResponseDTO.class);

        // Preenche nome do parceiro/empresa
        if (entity.getParceiro() != null) {
            dto.setNomeParceiroResponsavel(entity.getParceiro().getNomeEmpresa());
        }

        return dto;
    }
}
