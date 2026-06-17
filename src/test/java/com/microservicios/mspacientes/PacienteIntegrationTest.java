package com.microservicios.mspacientes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicios.mspacientes.dto.PacienteRegistroRequest;
import com.microservicios.mspacientes.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PacienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Test
    void debeRegistrarPacienteYGuardarEnBaseDeDatos() throws Exception {

        PacienteRegistroRequest request =
                new PacienteRegistroRequest(
                        "11111111-1",
                        "Marco",
                        "Perez",
                        LocalDate.of(1995, 1, 1),
                        "marco@test.cl",
                        "987654321",
                        "La Calera",
                        "123456"
                );

        mockMvc.perform(
                        post("/api/pacientes/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated());

        assertTrue(
                pacienteRepository.existsById("11111111-1")
        );
    }
}