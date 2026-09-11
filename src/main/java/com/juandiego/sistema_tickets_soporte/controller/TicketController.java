package com.juandiego.sistema_tickets_soporte.controller;

import com.juandiego.sistema_tickets_soporte.dto.CambiarEstadoDTO;
import com.juandiego.sistema_tickets_soporte.dto.TicketCreacionDTO;
import com.juandiego.sistema_tickets_soporte.dto.TicketResponseDTO;
import com.juandiego.sistema_tickets_soporte.model.Ticket;
import com.juandiego.sistema_tickets_soporte.model.Usuario;
import com.juandiego.sistema_tickets_soporte.service.TicketService;
import com.juandiego.sistema_tickets_soporte.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<TicketResponseDTO> crear(@Valid @RequestBody TicketCreacionDTO dto) {
        Usuario cliente = usuarioService.buscarPorId(dto.getClienteId());

        Ticket ticket = Ticket.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .prioridad(Ticket.Prioridad.valueOf(dto.getPrioridad().toUpperCase()))
                .cliente(cliente)
                .build();

        Ticket creado = ticketService.crear(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketResponseDTO.desde(creado));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> listarTodos() {
        List<TicketResponseDTO> tickets = ticketService.listarTodos().stream()
                .map(TicketResponseDTO::desde)
                .toList();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> buscarPorId(@PathVariable Long id) {
        Ticket ticket = ticketService.buscarPorId(id);
        return ResponseEntity.ok(TicketResponseDTO.desde(ticket));
    }

    @PatchMapping("/{id}/asignar/{agenteId}")
    public ResponseEntity<TicketResponseDTO> asignarAgente(
            @PathVariable Long id,
            @PathVariable Long agenteId) {
        Usuario agente = usuarioService.buscarPorId(agenteId);
        Ticket actualizado = ticketService.asignarAgente(id, agente);
        return ResponseEntity.ok(TicketResponseDTO.desde(actualizado));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<TicketResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoDTO dto) {
        Ticket.Estado nuevoEstado = Ticket.Estado.valueOf(dto.getEstado().toUpperCase());
        Ticket actualizado = ticketService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(TicketResponseDTO.desde(actualizado));
    }
}