package com.microservicios.mspacientes.dto;

public record PacienteLoginResponse(
    
    String token,
    String rut,
    String nombre
) {
}
