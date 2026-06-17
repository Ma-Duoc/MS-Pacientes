package com.microservicios.mspacientes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicios.mspacientes.dto.PacienteLoginRequest;
import com.microservicios.mspacientes.dto.PacienteLoginResponse;
import com.microservicios.mspacientes.dto.PacienteRegistroRequest;
import com.microservicios.mspacientes.dto.PacienteResponse;
import com.microservicios.mspacientes.exception.PacienteException;
import com.microservicios.mspacientes.security.JwtAuthenticationFilter;
import com.microservicios.mspacientes.service.JwtService;
import com.microservicios.mspacientes.service.PacienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PacienteController.class)
@AutoConfigureMockMvc(addFilters = false)
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PacienteService pacienteService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void debeRegistrarPaciente() throws Exception {

        PacienteRegistroRequest request =
                new PacienteRegistroRequest(
                        "12345678-9",
                        "Marco",
                        "Perez",
                        LocalDate.of(1990, 1, 1),
                        "marco@test.com",
                        "987654321",
                        "La Calera",
                        "123456"
                );

        PacienteResponse response =
                new PacienteResponse(
                        "12345678-9",
                        "Marco",
                        "Perez",
                        "marco@test.com",
                        "987654321",
                        "La Calera"
                );

        when(pacienteService.registrarPaciente(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/pacientes/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rut").value("12345678-9"))
                .andExpect(jsonPath("$.nombre").value("Marco"));
    }

    @Test
    void debeRealizarLogin() throws Exception {

        PacienteLoginRequest request =
                new PacienteLoginRequest(
                        "12345678-9",
                        "123456"
                );

        PacienteLoginResponse response =
                new PacienteLoginResponse(
                        "jwt-token",
                        "12345678-9",
                        "Marco"
                );

        when(pacienteService.login(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/pacientes/usuarios/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.rut").value("12345678-9"));
    }

    @Test
    void debeObtenerPacientePorRut() throws Exception {

        PacienteResponse response =
                new PacienteResponse(
                        "12345678-9",
                        "Marco",
                        "Perez",
                        "marco@test.com",
                        "987654321",
                        "La Calera"
                );

        when(pacienteService.obtenerPorRut("12345678-9"))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/pacientes/12345678-9")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("12345678-9"))
                .andExpect(jsonPath("$.nombre").value("Marco"));
    }

    @Test
    void debeRetornar404CuandoPacienteNoExiste() throws Exception {

        when(pacienteService.obtenerPorRut("99999999-9"))
                .thenThrow(
                        new PacienteException(
                                "Paciente no encontrado"
                        )
                );

        mockMvc.perform(
                        get("/api/pacientes/99999999-9")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void debeRetornarBadRequestCuandoRegistroEsInvalido() throws Exception {

        PacienteRegistroRequest request =
                new PacienteRegistroRequest(
                        "",
                        "",
                        "",
                        null,
                        "",
                        "",
                        "",
                        ""
                );

        mockMvc.perform(
                        post("/api/pacientes/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}