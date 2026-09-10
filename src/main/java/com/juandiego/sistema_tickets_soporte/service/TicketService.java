package com.juandiego.sistema_tickets_soporte.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.juandiego.sistema_tickets_soporte.exception.RecursoNoEncontradoException;
import com.juandiego.sistema_tickets_soporte.model.Ticket;
import com.juandiego.sistema_tickets_soporte.model.Usuario;
import com.juandiego.sistema_tickets_soporte.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public Ticket crear(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Ticket buscarPorId(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ticket no encontrado con id: " + id));
    }

    public List<Ticket> listarTodos() {
        return ticketRepository.findAll();
    }

    public List<Ticket> listarPorCliente(Usuario cliente) {
        return ticketRepository.findByCliente(cliente);
    }

    public List<Ticket> listarPorAgente(Usuario agente) {
        return ticketRepository.findByAgenteAsignado(agente);
    }

    public Ticket asignarAgente(Long ticketId, Usuario agente) {
        Ticket ticket = buscarPorId(ticketId);
        ticket.setAgenteAsignado(agente);
        ticket.setEstado(Ticket.Estado.EN_PROGRESO);
        return ticketRepository.save(ticket);
    }

    public Ticket cambiarEstado(Long ticketId, Ticket.Estado nuevoEstado) {
        Ticket ticket = buscarPorId(ticketId);
        ticket.setEstado(nuevoEstado);
        return ticketRepository.save(ticket);
    }
}