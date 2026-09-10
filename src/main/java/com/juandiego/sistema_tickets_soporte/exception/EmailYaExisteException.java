package com.juandiego.sistema_tickets_soporte.exception;

public class EmailYaExisteException extends RuntimeException {
    public EmailYaExisteException(String mensaje) {
        super(mensaje);
    }
}