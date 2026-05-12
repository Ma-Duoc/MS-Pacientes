package com.microservicios.mspacientes.security;

import com.microservicios.mspacientes.model.Paciente;
import com.microservicios.mspacientes.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final PacienteRepository pacienteRepository;

    @Override
    public UserDetails loadUserByUsername(String rut) throws UsernameNotFoundException {
        Paciente paciente = pacienteRepository.findByRut(rut)
                .orElseThrow(() -> new UsernameNotFoundException("Paciente no encontrado con RUT: " + rut));

        return User.builder()
                .username(paciente.getRut())
                .password(paciente.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }
}
