package com.microservicios.mspacientes.service;

import com.microservicios.mspacientes.dto.PacienteLoginRequest;
import com.microservicios.mspacientes.dto.PacienteLoginResponse;
import com.microservicios.mspacientes.dto.PacienteRegistroRequest;
import com.microservicios.mspacientes.dto.PacienteResponse;
import com.microservicios.mspacientes.exception.PacienteException;
import com.microservicios.mspacientes.model.Paciente;
import com.microservicios.mspacientes.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public PacienteResponse obtenerPorRut(String rut) {

    Paciente paciente = pacienteRepository.findById(rut)
            .orElseThrow(() -> new PacienteException("Paciente no encontrado con RUT: " + rut));

    return new PacienteResponse(
            paciente.getRut(),
            paciente.getNombre(),
            paciente.getApellido(),
            paciente.getEmail(),
            paciente.getTelefono(),
            paciente.getDireccion()
    );
}

    public boolean existsByRut(String rut) {
        return pacienteRepository.existsById(rut);
    }

    public boolean existsByEmail(String email) {
        return pacienteRepository.existsByEmail(email);
    }

    public PacienteResponse registrarPaciente(PacienteRegistroRequest request) {
        // Validar que el RUT no exista
        if (pacienteRepository.existsById(request.rut())) {
            throw new PacienteException("El RUT ya está registrado: " + request.rut());
        }

        // Validar que el email no exista
        if (pacienteRepository.existsByEmail(request.email())) {
            throw new PacienteException("El email ya está registrado: " + request.email());
        }

        // Validar que sea mayor de 18 años
        if (!esMayorDeEdad(request.fechaNacimiento())) {
            throw new PacienteException("El paciente debe ser mayor de 18 años");
        }

        // Encriptar password
        String passwordEncriptado = passwordEncoder.encode(request.password());

        // Crear y guardar paciente
        Paciente paciente = Paciente.builder()
                .rut(request.rut())
                .nombre(request.nombre())
                .apellido(request.apellido())
                .fechaNacimiento(request.fechaNacimiento())
                .email(request.email())
                .telefono(request.telefono())
                .direccion(request.direccion())
                .password(passwordEncriptado)
                .build();

        Paciente pacienteGuardado = pacienteRepository.save(paciente);

        // Retornar respuesta sin password
        return new PacienteResponse(
                pacienteGuardado.getRut(),
                pacienteGuardado.getNombre(),
                pacienteGuardado.getApellido(),
                pacienteGuardado.getEmail(),
                pacienteGuardado.getTelefono(),
                pacienteGuardado.getDireccion()
        );
    }

    public PacienteLoginResponse login(PacienteLoginRequest request) {
        // Buscar paciente por RUT
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(request.rut());

        if (pacienteOpt.isEmpty()) {
            throw new PacienteException("Credenciales inválidas");
        }

        Paciente paciente = pacienteOpt.get();

        // Validar password
        if (!passwordEncoder.matches(request.password(), paciente.getPassword())) {
            throw new PacienteException("Credenciales inválidas");
        }

        // Generar JWT token
        String token = jwtService.generateToken(paciente.getRut(), paciente.getEmail(), paciente.getNombre());

        // Retornar respuesta con JWT
        return new PacienteLoginResponse(
                token,
                paciente.getRut(),
                paciente.getNombre()
        );
    }

    private boolean esMayorDeEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return false;
        }

        LocalDate fechaActual = LocalDate.now();
        int edad = Period.between(fechaNacimiento, fechaActual).getYears();

        return edad >= 18;
    }

}
