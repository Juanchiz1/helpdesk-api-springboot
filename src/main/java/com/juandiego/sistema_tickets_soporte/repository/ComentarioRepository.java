package com.juandiego.sistema_tickets_soporte.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juandiego.sistema_tickets_soporte.model.Comentario;
import com.juandiego.sistema_tickets_soporte.model.Ticket;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByTicketOrderByFechaCreacionAsc(Ticket ticket);
}