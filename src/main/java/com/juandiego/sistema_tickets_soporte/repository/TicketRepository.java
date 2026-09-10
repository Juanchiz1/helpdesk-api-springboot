package com.juandiego.sistema_tickets_soporte.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juandiego.sistema_tickets_soporte.model.Ticket;
import com.juandiego.sistema_tickets_soporte.model.Usuario;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCliente(Usuario cliente);

    List<Ticket> findByAgenteAsignado(Usuario agente);

    List<Ticket> findByEstado(Ticket.Estado estado);
}
