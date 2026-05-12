package com.microservicios.mspacientes.controller;

import com.microservicios.mspacientes.dto.PacienteLoginRequest;
import com.microservicios.mspacientes.dto.PacienteLoginResponse;
import com.microservicios.mspacientes.dto.PacienteRegistroRequest;
import com.microservicios.mspacientes.dto.PacienteResponse;
import com.microservicios.mspacientes.exception.PacienteException;
import com.microservicios.mspacientes.service.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PacienteController {
    
    private final PacienteService pacienteService;

    // ---------------- AUTH ----------------

    @PostMapping("/usuarios/registro")
    public ResponseEntity<PacienteResponse> registrarPaciente(@Valid @RequestBody PacienteRegistroRequest request) {
        PacienteResponse response = pacienteService.registrarPaciente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/usuarios/login")
    public ResponseEntity<PacienteLoginResponse> login(@Valid @RequestBody PacienteLoginRequest request) {
        PacienteLoginResponse response = pacienteService.login(request);
        return ResponseEntity.ok(response);
    }

    // ---------------- 🔐 ENDPOINT PROTEGIDO ----------------

    @GetMapping("/perfil")
    public ResponseEntity<String> perfil(Authentication authentication) {
        
        // esto viene del SecurityContext (lo puso tu filtro JWT)
        String rut = authentication.getName();

        return ResponseEntity.ok("Acceso autorizado. RUT: " + rut);
    }

    @GetMapping("/{rut}")
    public ResponseEntity<PacienteResponse> obtenerPaciente(@PathVariable String rut) {
        try {
            PacienteResponse response = pacienteService.obtenerPorRut(rut);
            return ResponseEntity.ok(response);

        } catch (PacienteException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
}
    
}

