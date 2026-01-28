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
            // 1. Montar a entidade manualmente (mantido conforme original)
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

            if (e != null) {
                Endereco endereco = new Endereco();
                endereco.setEstado(e.getEstado());
                endereco.setCidade(e.getCidade());
                endereco.setBairro(e.getBairro());
                endereco.setRua(e.getRua());
                endereco.setNumero(e.getNumero());
                // Associar o endereço
                entity.setEndereco(endereco);
            }

            // =============================
            // 3. Vincular Gestor se logado
            // =============================
            if (usuarioSession.isLogado() && usuarioSession.getUsuarioLogado().getPessoa() instanceof Gestor) {
                entity.setGestor((Gestor) usuarioSession.getUsuarioLogado().getPessoa());
            }

            // =============================
            // 4. Chamar o service e Retornar
            // =============================

            PontoTuristico salvo = service.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(salvo));

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar o ponto turístico: " + ex.getMessage());
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PontoTuristicoResponseDTO> atualizar(@PathVariable Long id,
                                                               @RequestBody @Valid PontoTuristicoDTO dto) {
        // CORREÇÃO: Busca a entidade existente para não perder a referência do Endereço
        PontoTuristico entity = service.findById(id);

        if (entity == null) {
            return ResponseEntity.notFound().build();
        }

        // Atualiza campos simples
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setNivelAcessibilidade(dto.getNivelAcessibilidade());
        entity.setHorarioAbertura(dto.getHorarioAbertura());
        entity.setHorarioFechamento(dto.getHorarioFechamento());

        // CORREÇÃO: Atualiza os dados do endereço existente em vez de criar um novo (evita TransientObjectException)
        if (entity.getEndereco() != null && dto.getEndereco() != null) {
            entity.getEndereco().setRua(dto.getEndereco().getRua());
            entity.getEndereco().setNumero(dto.getEndereco().getNumero());
            entity.getEndereco().setBairro(dto.getEndereco().getBairro());
            entity.getEndereco().setCidade(dto.getEndereco().getCidade());
            entity.getEndereco().setEstado(dto.getEndereco().getEstado());
        } else if (dto.getEndereco() != null) {
            // Se não existia endereço antes, mapeia um novo
            entity.setEndereco(mapper.map(dto.getEndereco(), Endereco.class));
        }

        // Re-vincula gestor logado se necessário (regra de negócio de segurança)
        if (usuarioSession.isLogado() && usuarioSession.getUsuarioLogado().getPessoa() instanceof Gestor) {
            entity.setGestor((Gestor) usuarioSession.getUsuarioLogado().getPessoa());
        }

        service.update(entity);

        // Retorna o objeto atualizado convertido para DTO
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

    // Método auxiliar para converter Entity -> ResponseDTO
    private PontoTuristicoResponseDTO mapToResponse(PontoTuristico entity) {
        PontoTuristicoResponseDTO dto = mapper.map(entity, PontoTuristicoResponseDTO.class);
        // Garante que o nome do gestor seja preenchido se existir
        if (entity.getGestor() != null) {
            dto.setNomeGestorResponsavel(entity.getGestor().getNome());
        }
        return dto;
    }
}