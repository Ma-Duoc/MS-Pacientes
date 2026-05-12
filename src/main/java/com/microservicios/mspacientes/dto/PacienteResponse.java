package com.microservicios.mspacientes.dto;

public record PacienteResponse(
    
    String rut,
    String nombre,
    String apellido,
    String email,
    String telefono,
    String direccion
) {
}
