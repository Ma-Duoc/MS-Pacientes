package com.microservicios.mspacientes.security;

import com.microservicios.mspacientes.model.Paciente;
import com.microservicios.mspacientes.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Paciente paciente;

    @BeforeEach
    void setUp() {

        paciente = Paciente.builder()
                .rut("12345678-9")
                .nombre("Marco")
                .apellido("Perez")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .email("marco@test.com")
                .telefono("987654321")
                .direccion("La Calera")
                .password("passwordEncriptada")
                .build();
    }

    @Test
    void debeCargarUsuarioCorrectamente() {

        when(pacienteRepository.findByRut("12345678-9"))
                .thenReturn(Optional.of(paciente));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("12345678-9");

        assertNotNull(userDetails);

        assertEquals(
                "12345678-9",
                userDetails.getUsername()
        );

        assertEquals(
                "passwordEncriptada",
                userDetails.getPassword()
        );

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a ->
                                a.getAuthority().equals("ROLE_USER"))
        );
    }

    @Test
    void debeLanzarExcepcionCuandoPacienteNoExiste() {

        when(pacienteRepository.findByRut("99999999-9"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> customUserDetailsService
                                .loadUserByUsername("99999999-9")
                );

        assertEquals(
                "Paciente no encontrado con RUT: 99999999-9",
                exception.getMessage()
        );
    }
}
