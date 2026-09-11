package com.juandiego.sistema_tickets_soporte.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComentarioCreacionDTO {

    @NotBlank(message = "El contenido es obligatorio")
    private String contenido;

    @NotNull(message = "El id del autor es obligatorio")
    private Long autorId;
}