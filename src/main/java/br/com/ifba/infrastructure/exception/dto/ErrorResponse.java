package br.com.ifba.infrastructure.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ErrorResponse {

    private final int status;
    private final String message;
    private final LocalDateTime timestamp;
    // Usado condicionalmente no ApiExceptionHandler

    private String stacktrace; // Opcional, dependendo da flag printStackTrace

    // Construtor principal
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

}
