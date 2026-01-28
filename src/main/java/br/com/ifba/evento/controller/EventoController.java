package br.com.ifba.evento.controller;

import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.evento.dto.EventoDTO;
import br.com.ifba.evento.dto.EventoResponseDTO;
import br.com.ifba.evento.entity.Evento;
import br.com.ifba.evento.service.EventoIService;
import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.pessoa.entity.Pessoa;
import br.com.ifba.pessoa.other_users.gestor.entity.Gestor;
import br.com.ifba.pessoa.other_users.parceiro.entity.Parceiro;
import br.com.ifba.sessao.service.UsuarioSessionIService;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/eventos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EventoController {

    private final EventoIService service;
    private final UsuarioSessionIService usuarioSession;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventoResponseDTO> salvar(@RequestBody @Valid EventoDTO dto,
                                                    HttpServletRequest request) {

        // 1. Resgate da Sessão (Mantendo o que já funcionou)
        HttpSession session = request.getSession(false);
        String emailNaSessao = (session != null) ? (String) session.getAttribute("EMAIL_LOGADO") : null;

        if (emailNaSessao != null && !usuarioSession.isLogado()) {
            usuarioSession.setEmailLogado(emailNaSessao);
        }

        if (!usuarioSession.isLogado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioSession.getUsuarioLogado();

        // 2. O Segredo do 403: "Descascar" o objeto Pessoa
        Pessoa pessoa = usuario.getPessoa();

        // Se o Hibernate trouxe um Proxy, pegamos o objeto real de dentro dele
        if (pessoa instanceof HibernateProxy) {
            pessoa = (Pessoa) ((HibernateProxy) pessoa).getHibernateLazyInitializer().getImplementation();
        }

        // --- LOGS DETETIVE (Isso vai nos contar a verdade) ---
        System.out.println("DEBUG PERMISSÃO:");
        System.out.println("Classe real da Pessoa: " + pessoa.getClass().getName());
        System.out.println("É Parceiro? " + (pessoa instanceof Parceiro));
        System.out.println("É Gestor? " + (pessoa instanceof Gestor));
        // -----------------------------------------------------

        // 3. Verificação de Permissão Corrigida
        if (!(pessoa instanceof Parceiro || pessoa instanceof Gestor)) {
            System.out.println("ERRO 403: Usuário não é nem Parceiro nem Gestor.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Parceiro parceiroLogado = null;

        if (pessoa instanceof Parceiro) {
            parceiroLogado = (Parceiro) pessoa;
        } else if (pessoa instanceof Gestor) {
            // CORREÇÃO: Se for Gestor, permitimos salvar, mas passamos parceiro como null
            // (Assumindo que o service aceita null. Se não aceitar, teremos que ajustar o service)
            System.out.println("Usuário é Gestor. Salvando evento sem vincular a parceiro específico.");
            parceiroLogado = null;
        } else {
            // Caso de segurança extra
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 4. Salvar
        Evento evento = mapper.map(dto, Evento.class);
        Endereco endereco = mapper.map(dto.getEndereco(), Endereco.class);

        Evento saved = service.adicionarEvento(evento, parceiroLogado, endereco);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(saved));
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventoResponseDTO> atualizar(@PathVariable Long id,
                                                       @RequestBody @Valid EventoDTO dto) {
        if (!usuarioSession.isLogado()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // 1. Buscamos o evento existente no banco (Entity Gerenciado)
        Evento evento = service.findById(id);

        // Verifica se existe (embora o service geralmente lance exceção, é bom garantir)
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Atualizamos manualmente os campos do Evento (para não perder a referência do objeto)
        evento.setNome(dto.getNome());
        evento.setDescricao(dto.getDescricao());
        evento.setData(dto.getData());
        evento.setHora(dto.getHora());
        evento.setNivelAcessibilidade(dto.getNivelAcessibilidade());
        evento.setPublicoAlvo(dto.getPublicoAlvo());
        evento.setProgramacao(dto.getProgramacao());
        evento.setCategoria(dto.getCategoria());

        // 3. Atualizamos os dados do Endereço EXISTENTE
        Endereco endereco = evento.getEndereco();

        if (endereco != null) {
            endereco.setRua(dto.getEndereco().getRua());
            endereco.setNumero(dto.getEndereco().getNumero());
            endereco.setBairro(dto.getEndereco().getBairro());
            endereco.setCidade(dto.getEndereco().getCidade());
            endereco.setEstado(dto.getEndereco().getEstado());
        } else {
            // Caso raro onde o evento antigo não tinha endereço (legado), criamos um
            Endereco novoEndereco = mapper.map(dto.getEndereco(), Endereco.class);
            evento.setEndereco(novoEndereco);
            // Nota: Se cair aqui, pode dar erro se o service não salvar o endereço.
            // Mas em fluxo normal de edição, o endereço sempre existe.
        }

        // 4. Lógica de Segurança para Parceiro
        Usuario usuario = usuarioSession.getUsuarioLogado();
        if (usuario.getPessoa() instanceof Parceiro) {
            // Se quem edita é o parceiro, garante que o evento continue vinculado a ele
            evento.setParceiro((Parceiro) usuario.getPessoa());
        }
        // Se for Gestor, mantemos o parceiro que já estava no 'evento' recuperado do banco.

        // 5. Salva as alterações (Merge)
        service.save(evento);

        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<EventoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<EventoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    @GetMapping(value = "/find/buscar")
    public ResponseEntity<List<EventoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(
                service.findByNomeContainingIgnoreCase(nome).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping(value = "/find/categoria")
    public ResponseEntity<List<EventoResponseDTO>> buscarPorCategoria(@RequestParam String categoria) {
        return ResponseEntity.ok(
                service.findByCategoriaContainingIgnoreCase(categoria).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    private EventoResponseDTO mapToResponse(Evento entity) {
        EventoResponseDTO dto = mapper.map(entity, EventoResponseDTO.class);
        if (entity.getParceiro() != null) {
            dto.setNomeParceiroResponsavel(entity.getParceiro().getNomeEmpresa());
        }
        return dto;
    }
}
