package com.juandiego.sistema_tickets_soporte.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambiarEstadoDTO {

    @NotNull(message = "El nuevo estado es obligatorio")
    private String estado;
}