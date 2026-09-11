package com.juandiego.sistema_tickets_soporte.dto;

import com.juandiego.sistema_tickets_soporte.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TicketResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private String estado;
    private String prioridad;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Long clienteId;
    private String clienteNombre;
    private Long agenteId;
    private String agenteNombre;

    public static TicketResponseDTO desde(Ticket ticket) {
        return new TicketResponseDTO(
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getDescripcion(),
                ticket.getEstado().name(),
                ticket.getPrioridad().name(),
                ticket.getFechaCreacion(),
                ticket.getFechaActualizacion(),
                ticket.getCliente().getId(),
                ticket.getCliente().getNombre(),
                ticket.getAgenteAsignado() != null ? ticket.getAgenteAsignado().getId() : null,
                ticket.getAgenteAsignado() != null ? ticket.getAgenteAsignado().getNombre() : null
        );
    }
}