package br.com.ifba.pessoa.other_users.parceiro.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.pessoa.other_users.parceiro.dto.ParceiroCadastroDTO;
import br.com.ifba.pessoa.other_users.parceiro.dto.ParceiroResponseDTO;
import br.com.ifba.pessoa.other_users.parceiro.entity.Parceiro;
import br.com.ifba.pessoa.other_users.parceiro.service.ParceiroIService;
import br.com.ifba.pessoa.other_users.usuariocomum.dto.UsuarioComumResponseDTO;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.user.UsuarioIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/parceiros", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ParceiroController {

    private final ParceiroIService parceiroService;
    private final UsuarioIService usuarioService;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParceiroResponseDTO> salvar(@RequestBody @Valid ParceiroCadastroDTO dto) {
        Parceiro entity = mapper.map(dto, Parceiro.class);
        parceiroService.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(entity, ParceiroResponseDTO.class));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        parceiroService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<ParceiroResponseDTO>> listarTodos() {
        return ResponseEntity.ok(mapListToDto(parceiroService.findAll()));
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<ParceiroResponseDTO> buscarPorId(@PathVariable Long id) {
        Parceiro parceiro = parceiroService.findById(id);
        return ResponseEntity.ok(mapper.map(parceiro, ParceiroResponseDTO.class));
    }

    @GetMapping(value = "/find/buscar")
    public ResponseEntity<List<ParceiroResponseDTO>> buscarPorNome(@RequestParam("nome") String nome) {
        return ResponseEntity.ok(mapListToDto(parceiroService.findByNomeContainingIgnoreCase(nome)));
    }

    @GetMapping(value = "/find/cnpj/{cnpj}")
    public ResponseEntity<ParceiroResponseDTO> buscarPorCnpj(@PathVariable String cnpj) {
        return parceiroService.findByCnpj(cnpj)
                .map(p -> ResponseEntity.ok(mapper.map(p, ParceiroResponseDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- MÉTODOS DE TRANSIÇÃO (Sem @RequestBody de DTO, só lógica) ---

    @PostMapping(value = "/promover/{usuarioId}")
    public ResponseEntity<ParceiroResponseDTO> tornarParceiro(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioService.findById(usuarioId);
        Parceiro novoParceiro = parceiroService.tornarParceiro(usuario);
        return ResponseEntity.ok(mapper.map(novoParceiro, ParceiroResponseDTO.class));
    }

    @PostMapping(value = "/rebaixar/{parceiroId}")
    public ResponseEntity<UsuarioComumResponseDTO> removerParceiria(@PathVariable Long parceiroId) {
        Parceiro parceiro = parceiroService.findById(parceiroId);
        Usuario usuarioRebaixado = parceiroService.removerParceiria(parceiro);
        return ResponseEntity.ok(mapper.map(usuarioRebaixado.getPessoa(), UsuarioComumResponseDTO.class));
    }

    private List<ParceiroResponseDTO> mapListToDto(List<Parceiro> lista) {
        return lista.stream()
                .map(p -> mapper.map(p, ParceiroResponseDTO.class))
                .collect(Collectors.toList());
    }
}