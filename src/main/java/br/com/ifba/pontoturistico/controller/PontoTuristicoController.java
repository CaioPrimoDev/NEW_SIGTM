package br.com.ifba.pontoturistico.controller;

import br.com.ifba.endereco.dto.EnderecoCadastroDTO;
import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.pessoa.other_users.gestor.entity.Gestor;
import br.com.ifba.pontoturistico.dto.PontoTuristicoDTO;
import br.com.ifba.pontoturistico.dto.PontoTuristicoResponseDTO;
import br.com.ifba.pontoturistico.entity.PontoTuristico;
import br.com.ifba.pontoturistico.service.PontoTuristicoIService;
import br.com.ifba.sessao.entity.UsuarioSession;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/pontos-turisticos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PontoTuristicoController {

    private final PontoTuristicoIService service;
    private final UsuarioSession usuarioSession;
    private final ObjectMapperUtill mapper;

    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody PontoTuristicoDTO dto) {
        try {
            // =============================
            // 1. Montar a entidade manualmente
            // =============================

            PontoTuristico entity = new PontoTuristico();
            entity.setNome(dto.getNome());
            entity.setDescricao(dto.getDescricao());
            entity.setNivelAcessibilidade(dto.getNivelAcessibilidade());
            entity.setHorarioAbertura(dto.getHorarioAbertura());
            entity.setHorarioFechamento(dto.getHorarioFechamento());

            // =============================
            // 2. Criar o Endereço manualmente
            // =============================

            EnderecoCadastroDTO e = dto.getEndereco();

            Endereco endereco = new Endereco();
            endereco.setEstado(e.getEstado());
            endereco.setCidade(e.getCidade());
            endereco.setBairro(e.getBairro());
            endereco.setRua(e.getRua());
            endereco.setNumero(e.getNumero());

            // Associar o endereço
            entity.setEndereco(endereco);

            // =============================
            // 3. Chamar o service corretamente
            // =============================

            PontoTuristico salvo = service.save(entity);

            // =============================
            // 4. Retornar 201 Created
            // =============================

            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar o ponto turístico: " + ex.getMessage());
        }
    }


    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PontoTuristicoResponseDTO> atualizar(@PathVariable Long id,
                                                               @RequestBody @Valid PontoTuristicoDTO dto) {
        PontoTuristico entity = mapper.map(dto, PontoTuristico.class);
        entity.setId(id);

        // Re-vincula gestor logado se necessário
        if (usuarioSession.isLogado() && usuarioSession.getUsuarioLogado().getPessoa() instanceof Gestor) {
            entity.setGestor((Gestor) usuarioSession.getUsuarioLogado().getPessoa());
        }

        service.update(entity);
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        PontoTuristico entity = service.findById(id);
        service.delete(entity);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<PontoTuristicoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<PontoTuristicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    @GetMapping(value = "/find/buscar")
    public ResponseEntity<List<PontoTuristicoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(
                service.findByNomeStartingWithIgnoreCase(nome).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    private PontoTuristicoResponseDTO mapToResponse(PontoTuristico entity) {
        PontoTuristicoResponseDTO dto = mapper.map(entity, PontoTuristicoResponseDTO.class);
        if (entity.getGestor() != null) {
            dto.setNomeGestorResponsavel(entity.getGestor().getNome());
        }
        return dto;
    }
}