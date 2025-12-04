package br.com.ifba.pessoa.other_users.parceiro.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.pessoa.other_users.parceiro.dto.ParceiroCadastroDTO;
import br.com.ifba.pessoa.other_users.parceiro.dto.ParceiroResponseDTO;
import br.com.ifba.pessoa.other_users.parceiro.entity.Parceiro;
import br.com.ifba.pessoa.other_users.parceiro.service.ParceiroIService;
import br.com.ifba.pessoa.other_users.usuariocomum.dto.UsuarioComumResponseDTO;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.user.UsuarioIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/parceiros")
@RequiredArgsConstructor
public class ParceiroController {

    private final ParceiroIService parceiroService;
    private final UsuarioIService usuarioService; // Necessário para buscar o usuário na promoção
    private final ObjectMapperUtill mapper;

    // 1. SAVE (Criação direta ou Atualização)
    @PostMapping("/save")
    public ResponseEntity<ParceiroResponseDTO> salvar(@RequestBody ParceiroCadastroDTO dto) {
        Parceiro entity = mapper.map(dto, Parceiro.class);

        parceiroService.save(entity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(entity, ParceiroResponseDTO.class));
    }

    // 2. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        parceiroService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 3. LISTAR TODOS
    @GetMapping("/findall")
    public ResponseEntity<List<ParceiroResponseDTO>> listarTodos() {
        return ResponseEntity.ok(mapListToDto(parceiroService.findAll()));
    }

    // 4. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ParceiroResponseDTO> buscarPorId(@PathVariable Long id) {
        Parceiro parceiro = parceiroService.findById(id);
        return ResponseEntity.ok(mapper.map(parceiro, ParceiroResponseDTO.class));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ParceiroResponseDTO>> buscarPorNome(@RequestParam("nome") String nome) {

        // 1. Chama exatamente a função do Service que você mandou.
        // Se o nome for vazio ou não achar nada, o Service retorna lista vazia [].
        List<Parceiro> lista = parceiroService.findByNomeContainingIgnoreCase(nome);

        // 2. Converte a lista (vazia ou cheia) para DTOs e retorna 200 OK
        return ResponseEntity.ok(mapListToDto(lista));
    }

    // 6. BUSCAR POR CNPJ (Retorna Optional no service)
    @GetMapping("/buscar/cnpj/{cnpj}")
    public ResponseEntity<ParceiroResponseDTO> buscarPorCnpj(@PathVariable String cnpj) {
        return parceiroService.findByCnpj(cnpj)
                .map(p -> ResponseEntity.ok(mapper.map(p, ParceiroResponseDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- MÉTODOS DE TRANSIÇÃO (Lógica Complexa) ---

    /**
     * Transforma um Usuário Comum existente em Parceiro.
     * Requer que o Usuário já tenha uma Solicitação aprovada/existente (regra do Service).
     */
    @PostMapping("/promover/{usuarioId}")
    public ResponseEntity<ParceiroResponseDTO> tornarParceiro(@PathVariable Long usuarioId) {
        // 1. Busca o Usuário pelo ID (Service de Usuário)
        // Precisamos tratar a exceção caso o usuário não exista, ou assumir que o Service lança.
        // Como UsuarioService.findById retorna entidade, usamos ele.
        // Nota: Assumindo que você tem um método findById no UsuarioService exposto na Interface.
        // Se não tiver, precisará adicionar ou usar um repositório aqui (menos recomendado).
        Usuario usuario = usuarioService.findById(usuarioId);

        // 2. Chama a regra de negócio complexa
        Parceiro novoParceiro = parceiroService.tornarParceiro(usuario);

        // 3. Retorna os dados do novo parceiro
        return ResponseEntity.ok(mapper.map(novoParceiro, ParceiroResponseDTO.class));
    }

    /**
     * Remove a parceria, rebaixando o Parceiro para Usuário Comum.
     */
    @PostMapping("/rebaixar/{parceiroId}")
    public ResponseEntity<UsuarioComumResponseDTO> removerParceiria(@PathVariable Long parceiroId) {
        // 1. Busca o Parceiro existente
        Parceiro parceiro = parceiroService.findById(parceiroId);

        // 2. Chama a regra de negócio
        // O método retorna um 'Usuario'.
        Usuario usuarioRebaixado = parceiroService.removerParceiria(parceiro);

        // 3. Mapeia para um DTO de usuário (aqui estou mapeando para UsuarioComumResponseDTO
        // assumindo que a pessoa ligada ao usuário virou uma pessoa comum/genérica)
        // Nota: O seu service cria uma 'new Pessoa()', então precisamos mapear com cuidado.
        // Como UsuarioComumResponseDTO espera campos de Pessoa, isso deve funcionar.
        return ResponseEntity.ok(mapper.map(usuarioRebaixado.getPessoa(), UsuarioComumResponseDTO.class));
    }

    // --- Auxiliar ---
    private List<ParceiroResponseDTO> mapListToDto(List<Parceiro> lista) {
        return lista.stream()
                .map(p -> mapper.map(p, ParceiroResponseDTO.class))
                .collect(Collectors.toList());
    }
}