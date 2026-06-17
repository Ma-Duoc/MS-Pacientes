package com.microservicios.mspacientes.security;

import com.microservicios.mspacientes.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void debeContinuarCuandoNoExisteHeaderAuthorization()
            throws ServletException, IOException {

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void debeContinuarCuandoTokenEsInvalido()
            throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer token");

        when(jwtService.isTokenValid("token"))
                .thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void debeAutenticarCuandoTokenEsValido()
            throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer token");

        when(jwtService.isTokenValid("token"))
                .thenReturn(true);

        when(jwtService.extractRut("token"))
                .thenReturn("12345678-9");

        when(userDetailsService.loadUserByUsername("12345678-9"))
                .thenReturn(
                        new User(
                                "12345678-9",
                                "123",
                                Collections.emptyList()
                        )
                );

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(userDetailsService)
                .loadUserByUsername("12345678-9");

        verify(filterChain)
                .doFilter(request, response);
    }
}