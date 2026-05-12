package com.microservicios.mspacientes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record PacienteRegistroRequest(
    
    @NotBlank(message = "El RUT es obligatorio")
    String rut,
    
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,
    
    @NotBlank(message = "El apellido es obligatorio")
    String apellido,
    
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    LocalDate fechaNacimiento,
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    String email,
    
    String telefono,
    
    String direccion,
    
    @NotBlank(message = "El password es obligatorio")
    String password
) {
}
