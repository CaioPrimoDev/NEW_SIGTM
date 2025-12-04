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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaIService service;

    // Services necessários para buscar as entidades pelo ID
    private final PontoTuristicoIService pontoService;
    private final EventoIService eventoService;

    private final ObjectMapperUtill mapper;

    // 1. CRIAR RESERVA (Usuário Logado)
    @PostMapping("/save")
    public ResponseEntity<ReservaResponseDTO> salvar(@RequestBody ReservaCadastroDTO dto) {

        Reserva reserva = new Reserva();
        reserva.setDataReserva(dto.getDataReserva());

        // Lógica para decidir qual item vincular
        if (dto.getPontoTuristicoId() != null) {
            // Busca o Ponto (lança exceção se não existir)
            PontoTuristico ponto = pontoService.findById(dto.getPontoTuristicoId());
            reserva.setPontoTuristico(ponto);
        }
        else if (dto.getEventoId() != null) {
            // Busca o Evento (lança exceção se não existir)
            Evento evento = eventoService.findById(dto.getEventoId());
            reserva.setEvento(evento);
        } else {
            // Se nenhum ID for enviado, retorna Bad Request (ou deixa o Service validar)
            // Mas validar aqui economiza processamento.
            return ResponseEntity.badRequest().build();
        }

        // Service: Vincula usuário da sessão, gera token e salva
        service.save(reserva);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(reserva));
    }

    // 2. CANCELAR / DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        // O service.delete() não aceita ID direto, aceita Objeto.
        // Buscamos primeiro (o findById do service já valida permissão de dono/gestor)
        Reserva reserva = service.findById(id);
        service.delete(reserva);
        return ResponseEntity.noContent().build();
    }

    // 3. LISTAR (Minhas Reservas ou Todas se Gestor)
    @GetMapping("/findall")
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // 4. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(service.findById(id)));
    }

    // 5. BUSCAR POR TOKEN
    @GetMapping("/token/{token}")
    public ResponseEntity<List<ReservaResponseDTO>> buscarPorToken(@PathVariable String token) {
        return ResponseEntity.ok(
                service.findByTokenContainingIgnoreCase(token).stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList())
        );
    }

    // --- Mapeamento Inteligente (Customizado) ---
    private ReservaResponseDTO mapToResponse(Reserva entity) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(entity.getId());
        dto.setToken(entity.getToken());
        dto.setDataReserva(entity.getDataReserva());

        // Mapeia Usuário
        if (entity.getUsuario() != null && entity.getUsuario().getPessoa() != null) {
            dto.setNomeUsuario(entity.getUsuario().getPessoa().getNome());
        }

        // Mapeia Item (Lógica Híbrida)
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
