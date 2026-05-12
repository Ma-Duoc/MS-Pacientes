package com.microservicios.mspacientes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PacienteException extends RuntimeException {

    public PacienteException(String message) {
        super(message);
    }

    public PacienteException(String message, Throwable cause) {
        super(message, cause);
    }
}