package com.juandiego.sistema_tickets_soporte.dto;

import com.juandiego.sistema_tickets_soporte.model.Comentario;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ComentarioResponseDTO {

    private Long id;
    private String contenido;
    private LocalDateTime fechaCreacion;
    private String autorNombre;

    public static ComentarioResponseDTO desde(Comentario comentario) {
        return new ComentarioResponseDTO(
                comentario.getId(),
                comentario.getContenido(),
                comentario.getFechaCreacion(),
                comentario.getAutor().getNombre()
        );
    }
}