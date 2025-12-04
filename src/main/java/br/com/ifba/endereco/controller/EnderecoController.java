package br.com.ifba.endereco.controller;

import br.com.ifba.endereco.dto.EnderecoCadastroDTO;
import br.com.ifba.endereco.dto.EnderecoResponseDTO;
import br.com.ifba.endereco.entity.Endereco;
import br.com.ifba.endereco.service.EnderecoIService;
import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoIService service;
    private final ObjectMapperUtill mapper;

    // 1. SALVAR (Create)
    // Atenção: Se o endereço for duplicado, o Service lança IllegalStateException
    @PostMapping("/save")
    public ResponseEntity<EnderecoResponseDTO> salvar(@RequestBody EnderecoCadastroDTO dto) {
        Endereco entity = mapper.map(dto, Endereco.class);

        Endereco saved = service.save(entity);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.map(saved, EnderecoResponseDTO.class));
    }

    // 2. ATUALIZAR (Update)
    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> atualizar(@PathVariable Long id,
                                                         @RequestBody EnderecoCadastroDTO dto) {
        Endereco entity = mapper.map(dto, Endereco.class);
        entity.setId(id); // Garante que o ID da URL seja usado

        // O Service valida ID, duplicidade e campos obrigatórios
        Endereco updated = service.update(entity);

        return ResponseEntity.ok(mapper.map(updated, EnderecoResponseDTO.class));
    }

    // 3. DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 4. LISTAR TODOS
    @GetMapping("/findall")
    public ResponseEntity<List<EnderecoResponseDTO>> listarTodos() {
        List<Endereco> lista = service.findAll();

        List<EnderecoResponseDTO> dtos = lista.stream()
                .map(e -> mapper.map(e, EnderecoResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // 5. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> buscarPorId(@PathVariable Long id) {
        // Service lança NoSuchElementException se não achar (mapear para 404 no Handler)
        Endereco entity = service.findById(id);
        return ResponseEntity.ok(mapper.map(entity, EnderecoResponseDTO.class));
    }

    // 6. ENCONTRAR OU CRIAR (Opcional - mas útil baseado no seu Service)
    // Endpoint auxiliar para fronts que querem evitar erro de duplicidade
    @PostMapping("/encontrar-ou-criar")
    public ResponseEntity<EnderecoResponseDTO> encontrarOuCriar(@RequestBody EnderecoCadastroDTO dto) {
        Endereco entity = mapper.map(dto, Endereco.class);

        // Usa o método inteligente do service que não lança erro de duplicidade,
        // mas devolve o existente
        Endereco result = service.encontrarOuCriarEndereco(entity);

        return ResponseEntity.ok(mapper.map(result, EnderecoResponseDTO.class));
    }
}
