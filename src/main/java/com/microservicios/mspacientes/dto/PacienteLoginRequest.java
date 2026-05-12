package com.microservicios.mspacientes.dto;

import jakarta.validation.constraints.NotBlank;

public record PacienteLoginRequest(
    
    @NotBlank(message = "El RUT es obligatorio")
    String rut,
    
    @NotBlank(message = "El password es obligatorio")
    String password
) {
}
