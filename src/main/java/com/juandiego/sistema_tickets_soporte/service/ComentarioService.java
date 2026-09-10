package com.juandiego.sistema_tickets_soporte.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.juandiego.sistema_tickets_soporte.model.Comentario;
import com.juandiego.sistema_tickets_soporte.model.Ticket;
import com.juandiego.sistema_tickets_soporte.model.Usuario;
import com.juandiego.sistema_tickets_soporte.repository.ComentarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;

    public Comentario agregar(Ticket ticket, Usuario autor, String contenido) {
        Comentario comentario = Comentario.builder()
                .ticket(ticket)
                .autor(autor)
                .contenido(contenido)
                .build();
        return comentarioRepository.save(comentario);
    }

    public List<Comentario> listarPorTicket(Ticket ticket) {
        return comentarioRepository.findByTicketOrderByFechaCreacionAsc(ticket);
    }
}