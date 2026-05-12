package com.microservicios.mspacientes.repository;

import com.microservicios.mspacientes.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, String> {
    
    Optional<Paciente> findByEmail(String email);
    
    Optional<Paciente> findByRut(String rut);
    
    boolean existsByEmail(String email);
}
