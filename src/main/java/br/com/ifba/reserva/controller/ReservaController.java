package br.com.ifba.reserva.controller;

import br.com.ifba.evento.entity.Evento;
import br.com.ifba.evento.service.EventoIService;
import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.pontoturistico.entity.PontoTuristico;
import br.com.ifba.pontoturistico.service.PontoTuristicoIService;
import br.com.ifba.reserva.dto.ReservaCadastroDTO;
import br.com.ifba.reserva.dto.ReservaResponseDTO;
import br.com.ifba.reserva.entity.Reserva;
import br.com.ifba.reserva.service.ReservaIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/reservas", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaIService service;
    private final PontoTuristicoIService pontoService;
    private final EventoIService eventoService;
    private final ObjectMapperUtill mapper;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservaResponseDTO> salvar(@RequestBody @Valid ReservaCadastroDTO dto) {

        Reserva reserva = new Reserva();
        reserva.setDataReserva(dto.getDataReserva());

        // Graças ao @Valid e @OuPontoOuEvento, sabemos que esta lógica é segura
        if (dto.getPontoTuristicoId() != null) {
            PontoTuristico ponto = pontoService.findById(dto.getPontoTuristicoId());
            reserva.setPontoTuristico(ponto);
        } else if (dto.getEventoId() != null) {
            Evento evento = eventoService.findById(dto.getEventoId());
            reserva.setEvento(evento);
        }
        // O "else" final (ambos nulos) é impossível chegar aqui devido ao @Valid

        service.save(reserva);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(reserva));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        Reserva reserva = service.findById(id);
        service.delete(reserva);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/findall")
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<ReservaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    @GetMapping(value = "/find/token/{token}")
    public ResponseEntity<List<ReservaResponseDTO>> buscarPorToken(@PathVariable String token) {
        return ResponseEntity.ok(
                service.findByTokenContainingIgnoreCase(token).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    private ReservaResponseDTO mapToResponse(Reserva entity) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(entity.getId());
        dto.setToken(entity.getToken());
        dto.setDataReserva(entity.getDataReserva());

        if (entity.getUsuario() != null && entity.getUsuario().getPessoa() != null) {
            dto.setNomeUsuario(entity.getUsuario().getPessoa().getNome());
        }

        if (entity.getPontoTuristico() != null) {
            dto.setTipoItem("PONTO_TURISTICO");
            dto.setItemId(entity.getPontoTuristico().getId());
            dto.setNomeItem(entity.getPontoTuristico().getNome());
        } else if (entity.getEvento() != null) {
            dto.setTipoItem("EVENTO");
            dto.setItemId(entity.getEvento().getId());
            dto.setNomeItem(entity.getEvento().getNome());
        }
        return dto;
    }
}
