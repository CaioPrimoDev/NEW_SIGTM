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

        // 1. Resgate da Sessão (Mantido original)
        HttpSession session = request.getSession(false); //
        String emailNaSessao = (session != null) ? (String) session.getAttribute("EMAIL_LOGADO") : null; //

        if (emailNaSessao != null && !usuarioSession.isLogado()) { //
            usuarioSession.setEmailLogado(emailNaSessao); //
        } //

        if (!usuarioSession.isLogado()) { //
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //
        } //

        Usuario usuario = usuarioSession.getUsuarioLogado(); //
        Pessoa pessoa = descascarProxy(usuario.getPessoa()); // usando método auxiliar para Proxy

        // --- LOGS DETETIVE  ---
        System.out.println("DEBUG PERMISSÃO:"); //
        System.out.println("Classe real da Pessoa: " + pessoa.getClass().getName()); //
        System.out.println("É Parceiro? " + (pessoa instanceof Parceiro)); //
        System.out.println("É Gestor? " + (pessoa instanceof Gestor)); //
        // -----------------------------------------------------

        // 3. Verificação de Permissão Corrigida
        if (!(pessoa instanceof Parceiro || pessoa instanceof Gestor)) { //
            System.out.println("ERRO 403: Usuário não é nem Parceiro nem Gestor."); //
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); //
        } //

        Parceiro parceiroLogado = null; //

        if (pessoa instanceof Parceiro) { //
            parceiroLogado = (Parceiro) pessoa; //
        } else if (pessoa instanceof Gestor) { //
            // Gestor cadastra, mas não vincula a um parceiro próprio obrigatoriamente
            System.out.println("Usuário é Gestor. Salvando evento."); //
            parceiroLogado = null; //
        }

        // 4. Salvar
        Evento evento = mapper.map(dto, Evento.class); //
        Endereco endereco = mapper.map(dto.getEndereco(), Endereco.class); //

        Evento saved = service.adicionarEvento(evento, parceiroLogado, endereco); //

        return ResponseEntity.status(HttpStatus.CREATED) //
                .body(mapToResponse(saved)); //
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventoResponseDTO> atualizar(@PathVariable Long id,
                                                       @RequestBody @Valid EventoDTO dto) {
        if (!usuarioSession.isLogado()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // 1. Buscamos o evento existente no banco
        Evento evento = service.findById(id); //

        if (evento == null) { //
            return ResponseEntity.notFound().build(); //
        } //

        // Início da Verificação de permissão para Editar
        Usuario usuario = usuarioSession.getUsuarioLogado();
        Pessoa pessoa = descascarProxy(usuario.getPessoa());
        
        boolean isGestor = pessoa instanceof Gestor;
        boolean isDono = (pessoa instanceof Parceiro) && 
                         evento.getParceiro() != null && 
                         evento.getParceiro().getId().equals(pessoa.getId());

        // REGRA: Gestor edita tudo, Parceiro só o dele
        if (!isGestor && !isDono) {
            System.out.println("ERRO 403: Usuário tentou editar evento que não lhe pertence.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        //  Fim da verificação

        // 2. Atualizamos manualmente os campos do Evento
        evento.setNome(dto.getNome()); //
        evento.setDescricao(dto.getDescricao()); //
        evento.setData(dto.getData()); //
        evento.setHora(dto.getHora()); //
        evento.setNivelAcessibilidade(dto.getNivelAcessibilidade()); //
        evento.setPublicoAlvo(dto.getPublicoAlvo()); //
        evento.setProgramacao(dto.getProgramacao()); //
        evento.setCategoria(dto.getCategoria()); //

        // 3. Atualizamos os dados do Endereço
        Endereco endereco = evento.getEndereco(); //

        if (endereco != null) { //
            endereco.setRua(dto.getEndereco().getRua()); //
            endereco.setNumero(dto.getEndereco().getNumero()); //
            endereco.setBairro(dto.getEndereco().getBairro()); //
            endereco.setCidade(dto.getEndereco().getCidade()); //
            endereco.setEstado(dto.getEndereco().getEstado()); //
        } //

        // 4. Lógica de Segurança para Parceiro
        if (pessoa instanceof Parceiro) { //
            evento.setParceiro((Parceiro) pessoa); //
        } //

        // 5. Salva as alterações
        service.save(evento); //

        return ResponseEntity.ok(mapToResponse(service.findById(id))); //
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Início da Verificação de permissão para Deletar
        if (!usuarioSession.isLogado()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        Usuario usuario = usuarioSession.getUsuarioLogado();
        Pessoa pessoa = descascarProxy(usuario.getPessoa());

        // REGRA: Somente Gestor remove
        if (!(pessoa instanceof Gestor)) {
            System.out.println("ERRO 403: Parceiro tentou excluir um evento.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        //  Fim da verificação

        service.delete(id); //
        return ResponseEntity.noContent().build(); //
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
            // alterações jeff: preenchendo o ID do parceiro no DTO
            dto.setParceiroId(entity.getParceiro().getId());
        }
        return dto;
    }

    @GetMapping(value = "/futuros")
    public ResponseEntity<List<EventoResponseDTO>> listarFuturos() {
        return ResponseEntity.ok(
                service.findFuture().stream() // Chama o serviço novo
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // Método para lidar com Proxy do Hibernate de forma centralizada
    private Pessoa descascarProxy(Pessoa pessoa) {
        if (pessoa instanceof org.hibernate.proxy.HibernateProxy) {
            return (Pessoa) ((org.hibernate.proxy.HibernateProxy) pessoa)
                    .getHibernateLazyInitializer()
                    .getImplementation();
        }
        return pessoa;
    }
}
