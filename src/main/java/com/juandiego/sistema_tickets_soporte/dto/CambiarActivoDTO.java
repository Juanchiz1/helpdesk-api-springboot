package com.juandiego.sistema_tickets_soporte.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambiarActivoDTO {

    @NotNull(message = "El campo activo es obligatorio")
    private Boolean activo;
}