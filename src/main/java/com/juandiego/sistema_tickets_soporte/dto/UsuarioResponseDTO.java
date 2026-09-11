package com.juandiego.sistema_tickets_soporte.dto;

import com.juandiego.sistema_tickets_soporte.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private LocalDateTime fechaCreacion;

    public static UsuarioResponseDTO desde(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getFechaCreacion()
        );
    }
}
