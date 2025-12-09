package br.com.ifba.infrastructure.exception.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class ValidationExceptionDetails {
    LocalDateTime timestamp;
    int status;
    String title;
    String details;
    String developerMessage;
    List<String> fields;
    List<String> fieldsMessage;
}