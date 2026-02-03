package br.com.ifba.avaliacoes.controller;



import br.com.ifba.avaliacoes.dto.AvaliacaoDTO;

import br.com.ifba.avaliacoes.dto.AvaliacaoResponseDTO;

import br.com.ifba.avaliacoes.entity.Avaliacao;

import br.com.ifba.avaliacoes.service.AvaliacaoIService;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;

import br.com.ifba.sessao.entity.UsuarioSession;

import br.com.ifba.sessao.service.UsuarioSessionService;

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

@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)

@RequiredArgsConstructor

public class AvaliacaoController {



private final AvaliacaoIService service;

private final UsuarioSession usuarioSession;

private final ObjectMapperUtill mapper;



@PostMapping(

value = "/pontos-turisticos/{pontoId}/avaliacoes/save",

consumes = MediaType.APPLICATION_JSON_VALUE

)

public ResponseEntity<AvaliacaoResponseDTO> avaliarPonto(

@PathVariable Long pontoId,

@RequestBody @Valid AvaliacaoDTO dto) {



if (!usuarioSession.isLogado()) {

return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

}



Usuario usuarioLogado = usuarioSession.getUsuarioLogado();



Avaliacao entity = new Avaliacao();

entity.setEstrelas(dto.getEstrelas());

entity.setDescricao(dto.getDescricao());

entity.setUsuario(usuarioLogado);



if (usuarioLogado.getPessoa() != null) {

entity.setNomeAutor(usuarioLogado.getPessoa().getNome());

}



Avaliacao saved = service.saveForPonto(pontoId, entity);



return ResponseEntity

.status(HttpStatus.CREATED)

.body(mapToResponse(saved));

}



@GetMapping("/avaliacoes/find/me")

public ResponseEntity<List<AvaliacaoResponseDTO>> minhasAvaliacoes() {



if (!usuarioSession.isLogado()) {

return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

}



Long usuarioId = usuarioSession.getUsuarioLogado().getId();



return ResponseEntity.ok(

service.findByUsuarioId(usuarioId)

.stream()

.map(this::mapToResponse)

.collect(Collectors.toList())

);

}



private AvaliacaoResponseDTO mapToResponse(Avaliacao entity) {

AvaliacaoResponseDTO dto = mapper.map(entity, AvaliacaoResponseDTO.class);



if (entity.getPontoTuristico() != null) {

dto.setPontoTuristicoId(entity.getPontoTuristico().getId());

dto.setNomePontoTuristico(entity.getPontoTuristico().getNome());

}



return dto;

}

}