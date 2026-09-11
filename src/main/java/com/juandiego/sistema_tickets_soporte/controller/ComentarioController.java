package com.juandiego.sistema_tickets_soporte.controller;

import com.juandiego.sistema_tickets_soporte.dto.ComentarioCreacionDTO;
import com.juandiego.sistema_tickets_soporte.dto.ComentarioResponseDTO;
import com.juandiego.sistema_tickets_soporte.model.Comentario;
import com.juandiego.sistema_tickets_soporte.model.Ticket;
import com.juandiego.sistema_tickets_soporte.model.Usuario;
import com.juandiego.sistema_tickets_soporte.service.ComentarioService;
import com.juandiego.sistema_tickets_soporte.service.TicketService;
import com.juandiego.sistema_tickets_soporte.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final TicketService ticketService;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<ComentarioResponseDTO> agregar(
            @PathVariable Long ticketId,
            @Valid @RequestBody ComentarioCreacionDTO dto) {
        Ticket ticket = ticketService.buscarPorId(ticketId);
        Usuario autor = usuarioService.buscarPorId(dto.getAutorId());

        Comentario creado = comentarioService.agregar(ticket, autor, dto.getContenido());
        return ResponseEntity.status(HttpStatus.CREATED).body(ComentarioResponseDTO.desde(creado));
    }

    @GetMapping
    public ResponseEntity<List<ComentarioResponseDTO>> listarPorTicket(@PathVariable Long ticketId) {
        Ticket ticket = ticketService.buscarPorId(ticketId);
        List<ComentarioResponseDTO> comentarios = comentarioService.listarPorTicket(ticket).stream()
                .map(ComentarioResponseDTO::desde)
                .toList();
        return ResponseEntity.ok(comentarios);
    }
}