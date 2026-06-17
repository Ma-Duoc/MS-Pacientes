package com.microservicios.mspacientes.service;

import com.microservicios.mspacientes.dto.PacienteLoginRequest;
import com.microservicios.mspacientes.dto.PacienteLoginResponse;
import com.microservicios.mspacientes.dto.PacienteRegistroRequest;
import com.microservicios.mspacientes.dto.PacienteResponse;
import com.microservicios.mspacientes.exception.PacienteException;
import com.microservicios.mspacientes.model.Paciente;
import com.microservicios.mspacientes.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private PacienteService pacienteService;

    @Test
    void deberiaObtenerPacientePorRut() {

        Paciente paciente = Paciente.builder()
                .rut("11111111-1")
                .nombre("Marco")
                .apellido("Perez")
                .email("marco@test.com")
                .telefono("123456")
                .direccion("La Calera")
                .password("123")
                .build();

        when(pacienteRepository.findById("11111111-1"))
                .thenReturn(Optional.of(paciente));

        PacienteResponse response =
                pacienteService.obtenerPorRut("11111111-1");

        assertEquals("11111111-1", response.rut());
        assertEquals("Marco", response.nombre());
        assertEquals("Perez", response.apellido());

        verify(pacienteRepository).findById("11111111-1");
    }

    @Test
    void deberiaLanzarExcepcionCuandoPacienteNoExiste() {

        when(pacienteRepository.findById("999"))
                .thenReturn(Optional.empty());

        assertThrows(
                PacienteException.class,
                () -> pacienteService.obtenerPorRut("999")
        );
    }

    @Test
    void deberiaRetornarTrueSiRutExiste() {

        when(pacienteRepository.existsById("11111111-1"))
                .thenReturn(true);

        assertTrue(
                pacienteService.existsByRut("11111111-1")
        );
    }

    @Test
    void deberiaRetornarFalseSiRutNoExiste() {

        when(pacienteRepository.existsById("11111111-1"))
                .thenReturn(false);

        assertFalse(
                pacienteService.existsByRut("11111111-1")
        );
    }

    @Test
    void deberiaRetornarTrueSiEmailExiste() {

        when(pacienteRepository.existsByEmail("correo@test.com"))
                .thenReturn(true);

        assertTrue(
                pacienteService.existsByEmail("correo@test.com")
        );
    }

    @Test
    void deberiaRetornarFalseSiEmailNoExiste() {

        when(pacienteRepository.existsByEmail("correo@test.com"))
                .thenReturn(false);

        assertFalse(
                pacienteService.existsByEmail("correo@test.com")
        );
    }

    @Test
    void deberiaRegistrarPacienteCorrectamente() {

        PacienteRegistroRequest request =
                new PacienteRegistroRequest(
                        "11111111-1",
                        "Marco",
                        "Perez",
                        LocalDate.now().minusYears(25),
                        "marco@test.com",
                        "123456",
                        "La Calera",
                        "1234"
                );

        when(pacienteRepository.existsById(request.rut()))
                .thenReturn(false);

        when(pacienteRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode("1234"))
                .thenReturn("password_encriptada");

        Paciente pacienteGuardado =
                Paciente.builder()
                        .rut(request.rut())
                        .nombre(request.nombre())
                        .apellido(request.apellido())
                        .email(request.email())
                        .telefono(request.telefono())
                        .direccion(request.direccion())
                        .password("password_encriptada")
                        .build();

        when(pacienteRepository.save(any(Paciente.class)))
                .thenReturn(pacienteGuardado);

        PacienteResponse response =
                pacienteService.registrarPaciente(request);

        assertEquals("11111111-1", response.rut());
        assertEquals("Marco", response.nombre());

        verify(passwordEncoder).encode("1234");
        verify(pacienteRepository).save(any(Paciente.class));
    }

    @Test
    void deberiaLanzarExcepcionSiRutYaExiste() {

        PacienteRegistroRequest request =
                new PacienteRegistroRequest(
                        "11111111-1",
                        "Marco",
                        "Perez",
                        LocalDate.now().minusYears(25),
                        "marco@test.com",
                        "123456",
                        "La Calera",
                        "1234"
                );

        when(pacienteRepository.existsById(request.rut()))
                .thenReturn(true);

        assertThrows(
                PacienteException.class,
                () -> pacienteService.registrarPaciente(request)
        );
    }

    @Test
    void deberiaLanzarExcepcionSiEmailYaExiste() {

        PacienteRegistroRequest request =
                new PacienteRegistroRequest(
                        "11111111-1",
                        "Marco",
                        "Perez",
                        LocalDate.now().minusYears(25),
                        "marco@test.com",
                        "123456",
                        "La Calera",
                        "1234"
                );

        when(pacienteRepository.existsById(request.rut()))
                .thenReturn(false);

        when(pacienteRepository.existsByEmail(request.email()))
                .thenReturn(true);

        assertThrows(
                PacienteException.class,
                () -> pacienteService.registrarPaciente(request)
        );
    }

    @Test
    void deberiaLanzarExcepcionSiEsMenorDeEdad() {

        PacienteRegistroRequest request =
                new PacienteRegistroRequest(
                        "11111111-1",
                        "Marco",
                        "Perez",
                        LocalDate.now().minusYears(15),
                        "marco@test.com",
                        "123456",
                        "La Calera",
                        "1234"
                );

        when(pacienteRepository.existsById(request.rut()))
                .thenReturn(false);

        when(pacienteRepository.existsByEmail(request.email()))
                .thenReturn(false);

        assertThrows(
                PacienteException.class,
                () -> pacienteService.registrarPaciente(request)
        );
    }

    @Test
    void deberiaLoguearPacienteCorrectamente() {

        Paciente paciente =
                Paciente.builder()
                        .rut("11111111-1")
                        .nombre("Marco")
                        .email("marco@test.com")
                        .password("hash")
                        .build();

        PacienteLoginRequest request =
                new PacienteLoginRequest(
                        "11111111-1",
                        "1234"
                );

        when(pacienteRepository.findById("11111111-1"))
                .thenReturn(Optional.of(paciente));

        when(passwordEncoder.matches("1234", "hash"))
                .thenReturn(true);

        when(jwtService.generateToken(
                paciente.getRut(),
                paciente.getEmail(),
                paciente.getNombre()))
                .thenReturn("jwt-token");

        PacienteLoginResponse response =
                pacienteService.login(request);

        assertEquals("jwt-token", response.token());
        assertEquals("11111111-1", response.rut());
        assertEquals("Marco", response.nombre());
    }

    @Test
    void deberiaLanzarExcepcionCuandoRutNoExisteEnLogin() {

        PacienteLoginRequest request =
                new PacienteLoginRequest(
                        "11111111-1",
                        "1234"
                );

        when(pacienteRepository.findById("11111111-1"))
                .thenReturn(Optional.empty());

        assertThrows(
                PacienteException.class,
                () -> pacienteService.login(request)
        );
    }

    @Test
    void deberiaLanzarExcepcionCuandoPasswordEsIncorrecta() {

        Paciente paciente =
                Paciente.builder()
                        .rut("11111111-1")
                        .nombre("Marco")
                        .email("marco@test.com")
                        .password("hash")
                        .build();

        PacienteLoginRequest request =
                new PacienteLoginRequest(
                        "11111111-1",
                        "1234"
                );

        when(pacienteRepository.findById("11111111-1"))
                .thenReturn(Optional.of(paciente));

        when(passwordEncoder.matches("1234", "hash"))
                .thenReturn(false);

        assertThrows(
                PacienteException.class,
                () -> pacienteService.login(request)
        );
    }
}