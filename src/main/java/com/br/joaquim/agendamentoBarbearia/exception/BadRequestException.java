package com.br.joaquim.agendamentoBarbearia.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
