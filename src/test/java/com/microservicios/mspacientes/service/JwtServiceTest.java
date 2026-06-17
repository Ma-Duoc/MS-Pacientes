package com.microservicios.mspacientes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String SECRET =
            "miClaveSuperSecretaJWT_2026_segura_1234567890";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService,
                "secret",
                SECRET);

        ReflectionTestUtils.setField(jwtService,
                "expiration",
                86400L);
    }

    @Test
    void debeGenerarTokenValido() {

        String token = jwtService.generateToken(
                "12345678-9",
                "paciente@test.com",
                "Marco"
        );

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void debeExtraerRutCorrectamente() {

        String token = jwtService.generateToken(
                "12345678-9",
                "paciente@test.com",
                "Marco"
        );

        String rut = jwtService.extractRut(token);

        assertEquals("12345678-9", rut);
    }

    @Test
    void debeExtraerEmailCorrectamente() {

        String token = jwtService.generateToken(
                "12345678-9",
                "paciente@test.com",
                "Marco"
        );

        String email = jwtService.extractEmail(token);

        assertEquals("paciente@test.com", email);
    }

    @Test
    void debeExtraerNombreCorrectamente() {

        String token = jwtService.generateToken(
                "12345678-9",
                "paciente@test.com",
                "Marco"
        );

        String nombre = jwtService.extractNombre(token);

        assertEquals("Marco", nombre);
    }

    @Test
    void tokenRecienGeneradoDebeSerValido() {

        String token = jwtService.generateToken(
                "12345678-9",
                "paciente@test.com",
                "Marco"
        );

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void tokenCorruptoDebeSerInvalido() {

        String token = "token.invalido.prueba";

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void tokenManipuladoDebeSerInvalido() {

        String token = jwtService.generateToken(
                "12345678-9",
                "paciente@test.com",
                "Marco"
        );

        token = token + "abc";

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void debeTenerFechaExpiracion() {

        String token = jwtService.generateToken(
                "12345678-9",
                "paciente@test.com",
                "Marco"
        );

        assertNotNull(jwtService.extractExpiration(token));
    }
}